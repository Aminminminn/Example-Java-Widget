/*
 * Copyright 2013-2020 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.widget.container;

import ej.annotation.Nullable;
import ej.bon.XMath;
import ej.microui.MicroUI;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.event.Event;
import ej.microui.event.generator.Pointer;
import ej.mwt.Container;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.OutlineHelper;
import ej.mwt.util.Size;
import ej.widget.animation.AnimationListener;
import ej.widget.basic.drawing.Scrollbar;
import ej.widget.container.util.Scrollable;
import ej.widget.util.SwipeEventHandler;
import ej.widget.util.Swipeable;

/**
 * Allows to scroll a widget horizontally or vertically.
 * <p>
 * This scroll reuses the display buffer to scroll faster.
 * <p>
 * This requires that:
 * <ul>
 * <li>the background is not transparent,</li>
 * <li>the background is plain,</li>
 * <li>the screen buffer is fully readable.</li>
 * </ul>
 */
public class BufferedScroll extends Container {

	@Nullable
	private Widget child;
	@Nullable
	private Scrollable scrollableChild;
	private final Scrollbar scrollbar;
	private boolean showScrollbar;
	private boolean horizontal;

	// Swipe management.
	@Nullable
	private SwipeEventHandler swipeEventHandler;
	private final ScrollAssistant assistant;
	private int value;
	private boolean shifting;

	private int childCoordinate;
	private int previousPaintChildCoordinate;

	/**
	 * Creates a horizontal scroll container with a visible scrollbar.
	 */
	public BufferedScroll() {
		this(true, true);
	}

	/**
	 * Creates a scroll container specifying its orientation and the visibility of the scrollbar.
	 *
	 * @param horizontal
	 *            <code>true</code> to scroll horizontally, <code>false</code> to scroll vertically.
	 * @param showScrollbar
	 *            <code>true</code> to show the scrollbar, <code>false</code> otherwise.
	 */
	public BufferedScroll(boolean horizontal, boolean showScrollbar) {
		this.horizontal = horizontal;
		this.scrollbar = new Scrollbar(0);
		this.scrollbar.setHorizontal(horizontal);
		this.showScrollbar = showScrollbar;
		this.assistant = new ScrollAssistant();

		setEnabled(true);
		addChild(this.scrollbar);
	}

	@Override
	public boolean isTransparent() {
		assert !super.isTransparent();
		return false;
	}

	@Override
	protected void setShownChildren() {
		Widget child = this.child;
		if (child != null) {
			setShownChild(child);
		}
		if (this.showScrollbar) {
			setShownChild(this.scrollbar);
		}
	}

	/**
	 * Sets the child to scroll.
	 * <p>
	 * The given widget can implement {@link Scrollable} and be notified about when the visible area changes (for
	 * example for optimization purpose).
	 *
	 * @param child
	 *            the child to scroll.
	 */
	public void setChild(Widget child) {
		Widget oldChild = this.child;
		if (child != oldChild) {
			if (oldChild != null) {
				// replace old child by new child
				replaceChild(getChildIndex(oldChild), child);
			} else {
				// insert new child before scrollbar
				insertChild(child, 0);
			}

			// update fields
			this.child = child;
			if (child instanceof Scrollable) {
				this.scrollableChild = (Scrollable) child;
			} else {
				this.scrollableChild = null;
			}
		}
	}

	/**
	 * Sets the scroll orientation: horizontal or vertical.
	 *
	 * @param horizontal
	 *            <code>true</code> to scroll horizontally, <code>false</code> to scroll vertically.
	 */
	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
		this.scrollbar.setHorizontal(horizontal);
	}

	/**
	 * Sets whether the scrollbar is visible or not.
	 *
	 * @param show
	 *            <code>true</code> to show the scrollbar, <code>false</code> to hide it.
	 */
	public void showScrollbar(boolean show) {
		this.showScrollbar = show;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		int width = 0;
		int height = 0;

		Widget child = this.child;
		if (child != null) {
			int widthHint = size.getWidth();
			int heightHint = size.getHeight();

			if (this.horizontal) {
				computeChildOptimalSize(child, Widget.NO_CONSTRAINT, heightHint);
				computeChildOptimalSize(this.scrollbar, widthHint, Widget.NO_CONSTRAINT);
			} else {
				computeChildOptimalSize(child, widthHint, Widget.NO_CONSTRAINT);
				computeChildOptimalSize(this.scrollbar, Widget.NO_CONSTRAINT, heightHint);
			}

			width = child.getWidth();
			height = child.getHeight();
		}

		// Set container optimal size.
		size.setSize(width, height);
	}

	@Override
	protected void layOutChildren(int contentWidth, int contentHeight) {
		Scrollable scrollableChild = this.scrollableChild;
		if (scrollableChild != null) {
			scrollableChild.initializeViewport(contentWidth, contentHeight);
		}

		Widget child = this.child;
		int childOptimalWidth;
		int childOptimalHeight;
		if (child != null) {
			childOptimalWidth = child.getWidth();
			childOptimalHeight = child.getHeight();
		} else {
			childOptimalWidth = 0;
			childOptimalHeight = 0;
		}

		int excess;
		if (this.horizontal) {
			excess = childOptimalWidth - contentWidth;
			int scrollbarHeight = 0;
			if (excess > 0) {
				this.scrollbar.setMaximum(excess);
				if (this.showScrollbar) {
					scrollbarHeight = this.scrollbar.getHeight();
					this.layOutChild(this.scrollbar, 0, contentHeight - scrollbarHeight, contentWidth, scrollbarHeight);
				}
			}
			if (child != null) {
				layOutChild(child, 0, 0, childOptimalWidth, contentHeight - scrollbarHeight);
			}
		} else {
			excess = childOptimalHeight - contentHeight;
			int scrollbarWidth = 0;
			if (excess > 0) {
				this.scrollbar.setMaximum(excess);
				if (this.showScrollbar) {
					scrollbarWidth = this.scrollbar.getWidth();
					this.layOutChild(this.scrollbar, contentWidth - scrollbarWidth, 0, scrollbarWidth, contentHeight);
				}
			}
			if (child != null) {
				layOutChild(child, 0, 0, contentWidth - scrollbarWidth, childOptimalHeight);
			}
		}
		if (excess > 0) {
			SwipeEventHandler swipeEventHandler = this.swipeEventHandler;
			if (swipeEventHandler != null) {
				swipeEventHandler.stop();
			}

			swipeEventHandler = new SwipeEventHandler(excess, false, this.horizontal, this.assistant);
			swipeEventHandler.setAnimationListener(this.assistant);
			swipeEventHandler.moveTo(this.value);
			this.swipeEventHandler = swipeEventHandler;
		}

		int childCoordinate = -this.scrollbar.getValue();
		updateViewport(childCoordinate);
	}

	@Override
	public void render(GraphicsContext g) {
		int currentValue = -this.childCoordinate;
		int previousPaintValue = this.previousPaintChildCoordinate;
		this.previousPaintChildCoordinate = currentValue;
		if (previousPaintValue != Integer.MIN_VALUE) {
			int shift = currentValue - previousPaintValue;
			g.translate(getContentX(), getContentY());
			g.intersectClip(0, 0, getContentWidth(), getContentHeight());

			// Save paint context for the scrollbar rendering
			int translateX = g.getTranslationX();
			int translateY = g.getTranslationY();
			int x = g.getClipX();
			int y = g.getClipY();
			int width = g.getClipWidth();
			int height = g.getClipHeight();

			renderShiftedContent(g, shift);

			if (this.showScrollbar) {
				g.setTranslation(translateX, translateY);
				g.setClip(x, y, width, height);
				g.intersectClip(this.scrollbar.getX(), this.scrollbar.getY(), this.scrollbar.getWidth(),
						this.scrollbar.getHeight());
				Style style = getStyle();
				g.translate(-getContentX(), -getContentY());
				Size size = new Size(getWidth(), getHeight());
				OutlineHelper.applyOutlinesAndBackground(g, size, style);
				renderChild(this.scrollbar, g);
			}
		} else {
			super.render(g);
		}
	}

	private void renderShiftedContent(GraphicsContext g, int shift) {
		Widget child = this.child;
		assert child != null;

		// Copy the display content.
		int contentX = getContentX();
		int contentY = getContentY();
		int absoluteContentX = getAbsoluteX() + contentX;
		int absoluteContentY = getAbsoluteY() + contentY;
		int width = getContentWidth();
		int height = getContentHeight();
		if (this.horizontal) {
			Painter.drawDisplayRegion(g, absoluteContentX, absoluteContentY, width, height, -shift, 0);
			int xChild;
			int widthChild;
			if (shift > 0) {
				xChild = width - shift;
				widthChild = shift;
			} else {
				xChild = 0;
				widthChild = -shift;
			}
			g.intersectClip(xChild, 0, widthChild, child.getHeight());
		} else {
			Painter.drawDisplayRegion(g, absoluteContentX, absoluteContentY, width, height, 0, -shift);
			int yChild;
			int heightChild;
			if (shift > 0) {
				yChild = height - shift;
				heightChild = shift;
			} else {
				yChild = 0;
				heightChild = -shift;
			}
			g.intersectClip(0, yChild, child.getWidth(), heightChild);
		}

		// Draw the part of the child that appears.
		Style style = getStyle();
		g.translate(-contentX, -contentY);
		Size size = new Size(getWidth(), getHeight());
		OutlineHelper.applyOutlinesAndBackground(g, size, style);
		renderChild(child, g);
	}

	@Override
	protected void onAttached() {
		super.onAttached();

		// Force to paint all the first time.
		this.previousPaintChildCoordinate = Integer.MIN_VALUE;
	}

	@Override
	protected void onHidden() {
		super.onHidden();

		SwipeEventHandler swipeEventHandler = this.swipeEventHandler;
		if (swipeEventHandler != null) {
			swipeEventHandler.stop();
		}
	}

	/**
	 * Scrolls to a position.
	 *
	 * @param position
	 *            the x or y target (depending on the orientation).
	 * @param animate
	 *            whether the scrolling action should be animated.
	 */
	public void scrollTo(int position, boolean animate) {
		int max;
		Widget child = this.child;
		if (child != null) {
			if (this.horizontal) {
				max = child.getWidth() - getWidth();
			} else {
				max = child.getHeight() - getHeight();
			}
			max = Math.max(0, max);
		} else {
			max = 0;
		}
		position = XMath.limit(position, 0, max);
		this.scrollbar.setValue(position);
		SwipeEventHandler swipeEventHandler = this.swipeEventHandler;
		if (swipeEventHandler != null && animate) {
			swipeEventHandler.moveTo(position, SwipeEventHandler.DEFAULT_DURATION);
		} else {
			this.assistant.onMove(position);
		}
	}

	/**
	 * Scrolls to a position without animation.
	 *
	 * @param position
	 *            the x or y target (depending on the orientation).
	 */
	public void scrollTo(int position) {
		scrollTo(position, false);
	}

	@Override
	public boolean handleEvent(int event) {
		SwipeEventHandler swipeEventHandler = this.swipeEventHandler;
		if (swipeEventHandler != null && swipeEventHandler.handleEvent(event)) {
			return true;
		}
		if (Event.getType(event) == Pointer.EVENT_TYPE && Pointer.getAction(event) == Pointer.RELEASED) {
			this.previousPaintChildCoordinate = Integer.MIN_VALUE;
			requestRender();
		}
		return super.handleEvent(event);
	}

	private void updateViewport(int x, int y) {
		Scrollable scrollableChild = this.scrollableChild;
		if (scrollableChild != null) {
			scrollableChild.updateViewport(x, y);
		}
		Widget child = this.child;
		if (child != null) {
			child.setPosition(x, y);
		}
	}

	private void updateViewport(int childCoordinate) {
		Widget child = this.child;
		if (child != null) {
			if (this.horizontal) {
				updateViewport(childCoordinate, child.getY());
			} else {
				updateViewport(child.getX(), childCoordinate);
			}
		}
	}

	class ScrollAssistant implements Runnable, Swipeable, AnimationListener {

		@Override
		public void onAnimationStarted() {
			BufferedScroll.this.scrollbar.show();
		}

		@Override
		public void onAnimationStopped() {
			BufferedScroll.this.scrollbar.hide();
		}

		@Override
		public void run() {
			BufferedScroll scroll = BufferedScroll.this;
			scroll.shifting = false;
			if (scroll.isShown()) {
				scroll.scrollbar.setValue(scroll.value);
				int childCoordinate = (-scroll.value - scroll.scrollbar.getValue()) / 2;
				updateViewport(childCoordinate);
				scroll.childCoordinate = childCoordinate;
			}
		}

		@Override
		public void onMove(int position) {
			BufferedScroll scroll = BufferedScroll.this;
			if (scroll.value != position) {
				scroll.value = position;
				if (!scroll.shifting) {
					scroll.shifting = true;
					MicroUI.callSerially(this);
					requestRender();
				}
			}
		}

	}

}
