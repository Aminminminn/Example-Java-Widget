/*
 * Copyright 2021 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.widget.linechart.widget;

import com.microej.demo.widget.common.DottedLinePainter;

import ej.annotation.Nullable;
import ej.basictool.ArrayTools;
import ej.bon.XMath;
import ej.drawing.ShapePainter;
import ej.drawing.ShapePainter.Cap;
import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.motion.Motion;
import ej.motion.quad.QuadEaseInOutFunction;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Rectangle;
import ej.mwt.util.Size;
import ej.widget.util.motion.MotionAnimation;
import ej.widget.util.motion.MotionAnimationListener;
import ej.widget.util.render.StringPainter;

/**
 * Represents a line chart with several ordered points.
 */
public class LineChart extends Widget implements MotionAnimationListener {

	/**
	 * The extra style to set the color of the background graph lines.
	 */
	public static final int ID_GRAPH_LINE_COLOR = 1;
	/**
	 * The extra style to set the color of the point.
	 */
	public static final int ID_POINT_COLOR = 2;
	/**
	 * The extra style to set the color of the point when it's selected.
	 */
	public static final int ID_POINT_SELECTED_COLOR = 3;
	/**
	 * The extra style to set the color of the lines between points.
	 */
	public static final int ID_LINE_COLOR = 4;
	/**
	 * The extra style to radius of the drawn circle.
	 */
	public static final int ID_POINT_RADIUS = 5;

	private static final int DEFAULT_SCALE_COUNT = 5;
	private static final int DECIMALS_LONG_COUNT = 3;
	private static final String DECIMALS_SEPARATOR = "."; //$NON-NLS-1$

	private static final int PADDING_Y_BAR = 5;
	private static final int PADDING_X_BAR = 5;
	private static final int SCALE_LINE_DOT_LENGTH = 3;
	private static final int POINT_RADIUS = 4;
	private static final int LINE_THICKNESS = 1;
	private static final int LINE_FADE = 1;

	private static final int ANIMATION_DURATION = 600;
	private static final int ANIMATION_MIN = 0;
	private static final int ANIMATION_MAX = 100;

	private static final int DECIMAL = 10;

	/**
	 * List of all the ChartPoints displayed on the LineChart.
	 */
	protected ChartPoint[] points;

	private String unit;
	private int scaleCount;

	private int selectedChartPointIndex;

	private int currentApparitionStep;

	private final boolean drawCircle;

	/**
	 * Creates a LineChart Widget.
	 */
	public LineChart() {
		this(true);
	}

	/**
	 * Creates a LineChart Widget.
	 *
	 * @param drawCircle
	 *            if a circle should be drawn at the point.
	 */
	public LineChart(boolean drawCircle) {
		this.drawCircle = drawCircle;
		this.unit = ""; //$NON-NLS-1$
		this.scaleCount = DEFAULT_SCALE_COUNT;
		this.selectedChartPointIndex = -1;
		this.points = new ChartPoint[0];
	}

	/**
	 * Sets the unit.
	 *
	 * @param unit
	 *            the unit string.
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * Gets the unit.
	 *
	 * @return the unit string.
	 */
	public String getUnit() {
		return this.unit;
	}

	/**
	 * Sets the the number of values to show on the scale.
	 *
	 * @param scaleCount
	 *            the number of values to show on the scale.
	 */
	public void setScaleCount(int scaleCount) {
		this.scaleCount = scaleCount;
	}

	/**
	 * Gets the number of values shown on the scale.
	 *
	 * @return the number of values shown on the scale.
	 */
	public int getScaleCount() {
		return this.scaleCount;
	}

	/**
	 * Adds a point.
	 *
	 * @param chartPoint
	 *            the point to add
	 */
	public void addPoint(ChartPoint chartPoint) {
		this.points = ArrayTools.add(this.points, chartPoint);
	}

	/**
	 * Removes all points.
	 */
	public void clearPoints() {
		this.points = new ChartPoint[0];
	}

	/*
	 * Handle Selection of ChartPoints.
	 */

	/**
	 * Handles pointer events.
	 */
	@Override
	public boolean handleEvent(int event) {
		int type = Event.getType(event);
		if (type == Pointer.EVENT_TYPE) {
			int action = Buttons.getAction(event);
			if (action == Buttons.RELEASED || action == Pointer.DRAGGED) {
				Pointer pointer = (Pointer) Event.getGenerator(event);
				onPointerMoved(pointer.getX());
				return true;
			}
		}
		return super.handleEvent(event);
	}

	private void onPointerMoved(int pointerX) {

		Rectangle bound = getContentBounds();

		int xContentStart = getAbsoluteX() + bound.getX();
		pointerX = pointerX - xContentStart;

		Font font = getStyle().getFont();
		int yBarWidth = getYBarWidth(font, getTopScaleValue());
		int xStart = yBarWidth;

		int xEnd = bound.getWidth();

		if (pointerX >= xStart && pointerX < xEnd) {
			int selectedPoint = this.points.length * (pointerX - xStart) / (xEnd - xStart);
			selectPoint(selectedPoint);
		} else {
			selectPoint(-1);
		}
	}

	/**
	 * Selects one of the points.
	 *
	 * @param pointIndex
	 *            the index of the point to select or -1 to deselect all.
	 */
	public void selectPoint(int pointIndex) {
		@Nullable
		ChartPoint[] points = this.points;
		// check the index
		if (pointIndex > -1) {
			int pointIndexInt = pointIndex;
			if (pointIndexInt < 0 || pointIndexInt >= points.length) {
				throw new IndexOutOfBoundsException();
			}
		}
		int lastIndex = this.selectedChartPointIndex;

		if (pointIndex != lastIndex) {
			// unselect previously selected point
			if (lastIndex > -1) {
				ChartPoint oldPoint = points[lastIndex];
				oldPoint.setSelected(false);
			}

			// select newly selected point
			this.selectedChartPointIndex = pointIndex;
			if (pointIndex > -1) {
				ChartPoint newPoint = points[pointIndex];
				if (newPoint.getValue() < 0.0f) {
					this.selectedChartPointIndex = -1;
				} else {
					newPoint.setSelected(true);
				}
			}

			// repaint the chart
			requestRender();
		}
	}

	/*
	 * Animation.
	 */

	@Override
	public void onShown() {
		setEnabled(true); // Needs to be set to receive Events

		Motion motion = new Motion(QuadEaseInOutFunction.INSTANCE, ANIMATION_MIN, ANIMATION_MAX, ANIMATION_DURATION);
		MotionAnimation motionAnimation = new MotionAnimation(getDesktop().getAnimator(), motion, LineChart.this);
		motionAnimation.start();
	}

	@Override
	public void tick(int value, boolean finished) {
		this.currentApparitionStep = value;
		requestRender();
	}

	/**
	 * Gets the animation ratio.
	 *
	 * @return the animation ratio.
	 */
	protected float getAnimationRatio() {
		return (float) this.currentApparitionStep / ANIMATION_MAX;
	}

	/*
	 * Rendering of Chart.
	 */

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Always use full size. No change to size needed.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		Font font = style.getFont();
		int textColor = style.getColor();
		int graphLineColor = style.getExtraInt(ID_GRAPH_LINE_COLOR, textColor);

		float topValue = getTopScaleValue();

		int fontHeight = font.getHeight();
		int yBarWidth = getYBarWidth(font, topValue);
		int xBarHeight = fontHeight + PADDING_X_BAR;
		int yBarTopHeight = getTopBarHeight(fontHeight); // Space for Unit String & Selected Info
		int yBarBottom = contentHeight - xBarHeight;
		int innerChartWidth = contentWidth - yBarWidth;

		g.setColor(textColor);
		StringPainter.drawStringInArea(g, this.unit, font, 0, 0, yBarWidth - PADDING_Y_BAR, yBarTopHeight,
				Alignment.RIGHT, Alignment.TOP);

		drawSelectedPointInfo(g, font, contentWidth, yBarTopHeight);

		// draw Y values and lines
		int numScaleValues = this.scaleCount;
		for (int i = 0; i < numScaleValues + 1; i++) {
			float scaleValue = topValue * i / numScaleValues;
			String scaleString = toStringFloat(scaleValue, 0);
			int yScale = yBarBottom + (yBarTopHeight - yBarBottom) * i / numScaleValues;
			g.setColor(textColor);
			StringPainter.drawStringAtPoint(g, scaleString, font, yBarWidth - PADDING_Y_BAR, yScale, Alignment.RIGHT,
					Alignment.VCENTER);
			DottedLinePainter.drawHorizontalDottedLine(g, graphLineColor, yBarWidth, yScale, innerChartWidth,
					SCALE_LINE_DOT_LENGTH);
		}

		Rectangle chartBounds = new Rectangle(yBarWidth, yBarTopHeight, innerChartWidth,
				contentHeight - yBarTopHeight - xBarHeight);
		renderPointsAndLabel(g, style, chartBounds, contentHeight, topValue);
	}

	private void drawSelectedPointInfo(GraphicsContext g, Font font, int contentWidth, int height) {
		if (this.selectedChartPointIndex > -1) {
			ChartPoint selectedPoint = this.points[this.selectedChartPointIndex];
			String selected = selectedPoint.getFullName() + ": " //$NON-NLS-1$
					+ toStringFloat(selectedPoint.getValue(), DECIMALS_LONG_COUNT);
			StringPainter.drawStringInArea(g, selected, font, 0, 0, contentWidth, height, Alignment.HCENTER,
					Alignment.TOP);
		}
	}

	/**
	 * Renders the ChartPoints on the Chart.
	 *
	 * @param g
	 *            the GraphicsContext to use.
	 * @param style
	 *            the Style of the Widget.
	 * @param chartBounds
	 *            the bounds of the Charts content without y/x axis labels.
	 * @param contentHeight
	 *            the full height of the content.
	 * @param topValue
	 *            the top value of the scale.
	 */
	protected void renderPointsAndLabel(GraphicsContext g, Style style, Rectangle chartBounds, int contentHeight,
			float topValue) {
		ChartPoint[] points = this.points;

		// Styles
		Font font = style.getFont();
		int fontColor = style.getColor();
		int pointColor = style.getExtraInt(ID_POINT_COLOR, fontColor);
		int pointSelectedColor = style.getExtraInt(ID_POINT_SELECTED_COLOR, fontColor);
		int lineColor = style.getExtraInt(ID_LINE_COLOR, pointColor);
		int pointRadius = style.getExtraInt(ID_POINT_RADIUS, POINT_RADIUS);

		int yBottom = chartBounds.getY() + chartBounds.getHeight();
		float xStep = getStepSize(chartBounds.getWidth());
		float xPosStart = chartBounds.getX() + xStep / 2; // Add half a step at the start to center the bars

		int previousX = -1;
		int previousY = -1;
		float xPos = xPosStart;
		for (ChartPoint chartPoint : points) {
			float value = chartPoint.getValue();
			int currentX = (int) xPos;
			xPos += xStep;

			// Draw x-axis labels
			g.setColor(fontColor);
			StringPainter.drawStringAtPoint(g, chartPoint.getName(), font, currentX, contentHeight, Alignment.HCENTER,
					Alignment.BOTTOM);

			if (value < 0.0f) { // invalid point. don't draw this.
				previousX = -1;
				previousY = -1;
			} else {
				int finalLength = (int) ((yBottom - chartBounds.getY()) * value / topValue);
				int apparitionLength = (int) (finalLength * getAnimationRatio());
				int yTop = yBottom - apparitionLength;
				int currentY = yTop;

				if (previousY != -1) {
					g.setColor(lineColor);
					ShapePainter.drawThickFadedLine(g, previousX, previousY, currentX, currentY, LINE_THICKNESS,
							LINE_FADE, Cap.NONE, Cap.NONE);
				}

				previousX = currentX;
				previousY = currentY;
			}

		}

		// Circles are drawn after all lines have been drawn, since the line would otherwise overlap with the circle on
		// one end.
		if (this.drawCircle) {
			xPos = xPosStart;
			for (ChartPoint chartPoint : points) {
				float value = chartPoint.getValue();
				int currentX = (int) xPos;
				xPos += xStep;

				if (value < 0.0f) {
					continue;
				}

				int finalLength = (int) ((yBottom - chartBounds.getY()) * value / topValue);
				int apparitionLength = (int) (finalLength * getAnimationRatio());
				int yTop = yBottom - apparitionLength;
				int currentY = yTop;

				int centerX = currentX - pointRadius;
				int centerY = currentY - pointRadius;
				g.setColor(chartPoint.isSelected() ? pointSelectedColor : pointColor);
				Painter.fillCircle(g, centerX, centerY, pointRadius * 2);
			}
		}
	}

	/**
	 * Gets the height of the top bar for unit and selected label.
	 *
	 * @param fontHeight
	 *            the height of the font used for the labels.
	 * @return the height of the bar.
	 */
	private int getTopBarHeight(int fontHeight) {
		return fontHeight + fontHeight / 2;
	}

	/**
	 * Gets the width of the bar containing the y-axis labels.
	 *
	 * @param font
	 *            font used to display the labels.
	 * @param topValue
	 *            the top value on the scale.
	 * @return the width of the y-axis bar.
	 */
	private int getYBarWidth(Font font, float topValue) {
		String topValueString = toStringFloat(topValue, 0);
		int widthUnit = font.stringWidth(this.unit);
		int widthValue = font.stringWidth(topValueString);
		return XMath.max(widthUnit, widthValue) + PADDING_Y_BAR;
	}

	/**
	 * Gets the step size between points.
	 *
	 * @param width
	 *            the width of the charts content bounds excluding y axis labels.
	 * @return the width between points.
	 */
	protected float getStepSize(int width) {
		return (float) width / this.points.length;
	}

	/*
	 * Other help function.
	 */

	/**
	 * Converts float to string.
	 *
	 * @param value
	 *            the value to convert to String.
	 * @param decimals
	 *            the number of decimal places (0 = No decimal).
	 * @return the float as string.
	 */
	protected static String toStringFloat(float value, int decimals) {
		StringBuilder builder = new StringBuilder();
		builder.append((int) value);
		if (decimals > 0) {
			builder.append(DECIMALS_SEPARATOR);
			for (int i = 0; i < decimals; i++) {
				value *= DECIMAL;
				builder.append((int) value % DECIMAL);
			}
		}
		return builder.toString();
	}

	/**
	 * Gets the highest value of all points on the chart.
	 *
	 * @return the highest value.
	 */
	private float getMaxPointValue() {
		float maxValue = 0.0f;
		ChartPoint[] points = this.points;
		for (ChartPoint point : points) {
			maxValue = Math.max(maxValue, point.getValue());
		}
		return maxValue;
	}

	/**
	 * Gets the top value of the scale This implementation takes the 2 most-meaningful digits of the max value and
	 * returns the next divisor of getNumValues().
	 *
	 * @return the top value of the scale.
	 */
	private float getTopScaleValue() {
		int numValues = this.scaleCount;
		float val = getMaxPointValue();
		float multiplier = 1.0f;
		while (val < DECIMAL) {
			val *= DECIMAL;
			multiplier /= DECIMAL;
		}
		while (val > DECIMAL * (2 * numValues)) {
			val /= DECIMAL;
			multiplier *= DECIMAL;
		}
		int n = (int) Math.ceil(val);
		int extra = n % numValues;
		if (extra > 0) {
			n += numValues - extra;
		}
		return n * multiplier;
	}
}
