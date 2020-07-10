/*
 * Copyright 2020 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package com.microej.demo.widget.main;

import com.microej.demo.widget.common.DemoColors;
import com.microej.demo.widget.common.Fonts;
import com.microej.demo.widget.common.Navigation;
import com.microej.demo.widget.common.Page;
import com.microej.demo.widget.common.PageHelper;
import com.microej.demo.widget.common.Pages;
import com.microej.demo.widget.main.style.GoToBackground;
import com.microej.demo.widget.main.widget.MenuItem;
import com.microej.demo.widget.main.widget.Scroll;
import com.microej.demo.widget.main.widget.ScrollableList;
import com.microej.demo.widget.main.widget.Scrollbar;

import ej.microui.MicroUI;
import ej.microui.display.Colors;
import ej.mwt.Desktop;
import ej.mwt.Widget;
import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.RectangularBackground;
import ej.mwt.style.dimension.FixedDimension;
import ej.mwt.style.outline.FlexibleOutline;
import ej.mwt.style.outline.UniformOutline;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.OddChildSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.stylesheet.selector.combinator.AndCombinator;
import ej.mwt.util.Alignment;
import ej.widget.container.util.LayoutOrientation;
import ej.widget.listener.OnClickListener;

/**
 * Page that allows to navigate to other pages.
 */
public class MainPage implements Page {

	private static final int LIST_ITEM = 70898;

	private static final int GRAY = 0xe5e9eb;

	/**
	 * Shows the main page.
	 *
	 * @param args
	 *            not used.
	 */
	public static void main(String[] args) {
		MicroUI.start();
		Desktop desktop = new MainPage().getDesktop();
		desktop.requestShow();
	}

	@Override
	public Desktop getDesktop() {
		Desktop desktop = PageHelper.createDesktop();

		CascadingStylesheet stylesheet = createStylesheet();
		desktop.setStylesheet(stylesheet);

		Widget pageContent = createPageContent();
		desktop.setWidget(pageContent);

		return desktop;
	}

	private CascadingStylesheet createStylesheet() {
		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getDefaultStyle();
		style.setColor(0x4b5357);
		style.setBackground(new RectangularBackground(DemoColors.EMPTY_SPACE));
		style.setFont(Fonts.getDefaultFont());
		style.setHorizontalAlignment(Alignment.HCENTER);
		style.setVerticalAlignment(Alignment.VCENTER);

		style = stylesheet.getSelectorStyle(new TypeSelector(Scrollbar.class));
		style.setDimension(new FixedDimension(2, Widget.NO_CONSTRAINT));
		style.setPadding(new UniformOutline(1));
		style.setColor(GRAY);

		style = stylesheet.getSelectorStyle(new ClassSelector(LIST_ITEM));
		style.setPadding(new FlexibleOutline(6, 0, 5, 24));
		style.setHorizontalAlignment(Alignment.LEFT);
		style.setBackground(new GoToBackground(Colors.WHITE));

		style = stylesheet
				.getSelectorStyle(new AndCombinator(new ClassSelector(LIST_ITEM), OddChildSelector.ODD_CHILD_SELECTOR));
		style.setBackground(new GoToBackground(GRAY));

		PageHelper.addCommonStyle(stylesheet);

		return stylesheet;
	}

	private Widget createPageContent() {
		Scroll scroll = new Scroll(LayoutOrientation.VERTICAL);
		ScrollableList list = new ScrollableList(LayoutOrientation.VERTICAL);
		scroll.setChild(list);
		for (int i = 0; i < Pages.ALL_PAGES.length; i++) {
			final String pageName = Pages.ALL_PAGES[i];
			MenuItem goToPage = new MenuItem(pageName);
			goToPage.addClassSelector(LIST_ITEM);
			list.addChild(goToPage);
			goToPage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick() {
					Navigation.showPage(Pages.getPage(pageName));
				}
			});
		}
		for (int i = Pages.ALL_PAGES.length; i < 100; i++) {
			MenuItem menuItem = new MenuItem("Stub Page " + i); //$NON-NLS-1$
			menuItem.addClassSelector(LIST_ITEM);
			list.addChild(menuItem);
		}

		Widget pageContent = PageHelper.createPage(scroll, false);
		return pageContent;
	}

}
