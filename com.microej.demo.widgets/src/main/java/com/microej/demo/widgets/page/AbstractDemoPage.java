/*
 * Java
 *
 * Copyright 2014-2015 IS2T. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found at http://www.is2t.com/open-source-bsd-license/.
 */
package com.microej.demo.widgets.page;

import com.microej.demo.exit.ExitHandler;
import com.microej.demo.widgets.WidgetsDemo;
import com.microej.demo.widgets.style.ClassSelectors;
import com.microej.demo.widgets.style.Images;
import com.microej.demo.widgets.style.Pictos;

import ej.components.dependencyinjection.ServiceLoaderFactory;
import ej.container.OppositeBars;
import ej.mwt.Desktop;
import ej.mwt.MWT;
import ej.mwt.Widget;
import ej.navigation.page.Page;
import ej.widget.basic.Image;
import ej.widget.basic.Label;
import ej.widget.basic.image.ImageHelper;
import ej.widget.composed.Button;
import ej.widget.composed.ButtonComposite;
import ej.widget.listener.OnClickListener;

/**
 * Common abstract page implementation for all the application pages.
 */
public abstract class AbstractDemoPage extends Page {

	private OppositeBars content;

	/**
	 * Creates a new demo page.
	 */
	public AbstractDemoPage() {
		setWidget(createContent());
	}

	@Override
	public void onTransitionStart() {
		super.onTransitionStart();
		hideNotify();
	}

	@Override
	public void onTransitionStop() {
		super.onTransitionStop();
		if (isShown()) {
			showNotify();
		}
	}

	// @Override
	// public void showNotify() {
	// super.showNotify();
	// System.gc();
	// Runtime runtime = Runtime.getRuntime();
	// System.out.println(runtime.totalMemory() - runtime.freeMemory() + "b");
	// }

	@Override
	public void show(Desktop desktop) throws NullPointerException {
		this.content.add(createTopBar(), MWT.NORTH);
		super.show(desktop);
	}

	private Widget createContent() {
		this.content = new OppositeBars();
		this.content.setHorizontal(false);
		this.content.add(createTopBar(), MWT.NORTH);
		this.content.add(createMainContent(), MWT.CENTER);
		return this.content;
	}

	/**
	 * Creates the widget representing the top bar of the page.
	 *
	 * @return the top bar widget.
	 */
	protected Widget createTopBar() {
		// The title of the page.
		Label titleLabel = new Label(getTitle());
		titleLabel.addClassSelector(ClassSelectors.TITLE);

		OppositeBars topBar = new OppositeBars();
		topBar.add(titleLabel, MWT.CENTER);

		if (WidgetsDemo.canGoBack()) {
			// Add a back button.
			Button backButton = new Button(Character.toString(Pictos.BACK));
			backButton.getLabel().addClassSelector(ClassSelectors.LARGE_ICON);
			backButton.addOnClickListener(new OnClickListener() {

				@Override
				public void onClick() {
					WidgetsDemo.back();
				}
			});
			topBar.add(backButton, MWT.WEST);
		} else {
			// Add an exit button.
			ButtonComposite exitButton = new ButtonComposite();
			exitButton.addOnClickListener(new OnClickListener() {

				@Override
				public void onClick() {
					ExitHandler exitHandler = ServiceLoaderFactory.getServiceLoader().getService(ExitHandler.class);
					if (exitHandler != null) {
						exitHandler.exit();
					}
				}
			});
			Image exitIcon = new Image(ImageHelper.loadImage(Images.MICROEJ_LOGO));
			exitButton.setWidget(exitIcon);
			topBar.add(exitButton, MWT.WEST);
		}
		return topBar;
	}

	/**
	 * Gets the title of the page.
	 *
	 * @return the title of the page.
	 */
	protected abstract String getTitle();

	/**
	 * Creates the widget representing the main content of the page.
	 *
	 * @return the composite representing the content of the page.
	 */
	protected abstract Widget createMainContent();

	@Override
	public String getCurrentURL() {
		return getClass().getName();
	}

}
