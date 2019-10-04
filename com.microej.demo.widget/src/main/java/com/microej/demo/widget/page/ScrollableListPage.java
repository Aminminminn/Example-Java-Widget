/*
 * Java
 *
 * Copyright  2015-2019 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package com.microej.demo.widget.page;

import com.microej.demo.widget.style.ClassSelectors;

import ej.bon.Timer;
import ej.bon.TimerTask;
import ej.components.dependencyinjection.ServiceLoaderFactory;
import ej.microui.display.Display;
import ej.widget.basic.Label;
import ej.widget.container.List;
import ej.widget.container.Scroll;

/**
 * This page illustrates the scrollable list.
 */
public class ScrollableListPage extends AbstractDemoPage {

	private static final String ITEM_PREFIX = "Item "; //$NON-NLS-1$
	private static final int APPEARANCE_DELAY = 1000;
	private static final int ITEM_COUNT = 100;
	private static final int FIRST_SHOT_COUNT = 20;

	private final List listComposite;
	private boolean complete;

	/**
	 * Creates a scrollable list page.
	 */
	public ScrollableListPage() {
		super(false, "Scrollable list"); //$NON-NLS-1$

		// layout:
		// Item 1
		// Item 2
		// ...
		// Item n-1
		// Item n
		this.listComposite = new List(false);

		addItems(1, FIRST_SHOT_COUNT);

		Scroll scroll = new Scroll(false, true);
		scroll.setWidget(this.listComposite);
		setCenter(scroll);
	}

	private void addItems(int start, int end) {
		for (int i = start; i <= end; i++) {
			Label item = new Label(ITEM_PREFIX + i);
			item.addClassSelector(ClassSelectors.LIST_ITEM);
			this.listComposite.add(item);
		}
	}

	@Override
	public void showNotify() {
		super.showNotify();
		if (!ScrollableListPage.this.complete) {
			// Add missing items.
			Timer timer = ServiceLoaderFactory.getServiceLoader().getService(Timer.class);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (isShown()) {
						Display.getDefaultDisplay().callSerially(new Runnable() {
							@Override
							public void run() {
								if (!ScrollableListPage.this.complete) {
									ScrollableListPage.this.complete = true;
									addItems(FIRST_SHOT_COUNT + 1, ITEM_COUNT);
								}
							}
						});
						revalidate();
					}
				}
			}, APPEARANCE_DELAY);
		}
	}

}
