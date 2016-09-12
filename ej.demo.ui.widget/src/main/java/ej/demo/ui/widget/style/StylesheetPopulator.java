/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.demo.ui.widget.style;

import ej.microui.display.Font;
import ej.microui.display.GraphicsContext;
import ej.mwt.MWT;
import ej.style.State;
import ej.style.Stylesheet;
import ej.style.background.NoBackground;
import ej.style.background.PlainBackground;
import ej.style.border.ComplexRectangularBorder;
import ej.style.border.SimpleRectangularBorder;
import ej.style.border.SimpleRoundedBorder;
import ej.style.dimension.FixedDimension;
import ej.style.font.FontProfile;
import ej.style.font.FontProfile.FontSize;
import ej.style.outline.ComplexOutline;
import ej.style.outline.SimpleOutline;
import ej.style.selector.ClassSelector;
import ej.style.selector.EvenChildSelector;
import ej.style.selector.StateSelector;
import ej.style.selector.TypeSelector;
import ej.style.selector.combinator.AndCombinator;
import ej.style.text.ComplexTextManager;
import ej.style.util.EditableStyle;
import ej.style.util.StyleHelper;
import ej.widget.basic.Image;
import ej.widget.basic.Label;
import ej.widget.basic.drawing.CheckBox;
import ej.widget.basic.drawing.CircularProgressBar;
import ej.widget.basic.drawing.ProgressBar;
import ej.widget.basic.drawing.RadioBox;
import ej.widget.basic.drawing.Slider;
import ej.widget.basic.drawing.SwitchBox;
import ej.widget.basic.image.ImageCheck;
import ej.widget.basic.image.ImageRadio;
import ej.widget.basic.image.ImageSlider;
import ej.widget.basic.image.ImageSwitch;
import ej.widget.basic.picto.PictoCheck;
import ej.widget.basic.picto.PictoProgress;
import ej.widget.basic.picto.PictoRadio;
import ej.widget.basic.picto.PictoSlider;
import ej.widget.basic.picto.PictoSwitch;

/**
 * Class responsible for initializing the demo styles.
 */
public class StylesheetPopulator {

	private static final int FOREGROUND = MicroEJColors.WHITE;
	private static final int BACKGROUND = MicroEJColors.CONCRETE_BLACK_75;
	private static final int LIST_EVEN_BACKGROUND = MicroEJColors.CONCRETE_BLACK_50;
	private static final int TITLE_BORDER = MicroEJColors.CONCRETE_BLACK_25;
	private static final int CHECKED_FOREGROUND = MicroEJColors.BONDI;
	private static final int UNCHECKED_FOREGROUND = MicroEJColors.CONCRETE_BLACK_25;
	private static final int ACTIVE_FOREGROUND = MicroEJColors.TURQUOISE;

	// Prevents initialization.
	private StylesheetPopulator() {
	}

	/**
	 * Populates the stylesheet.
	 */
	public static void initialize() {
		Stylesheet stylesheet = StyleHelper.getStylesheet();

		// Sets the default style.
		EditableStyle defaultStyle = new EditableStyle();
		defaultStyle.setForegroundColor(FOREGROUND);
		defaultStyle.setBackgroundColor(BACKGROUND);
		FontProfile defaultFontProfile = new FontProfile();
		defaultFontProfile.setFamily(FontFamilies.ROBOTO);
		defaultFontProfile.setSize(FontSize.MEDIUM);
		defaultStyle.setFontProfile(defaultFontProfile);
		// defaultStyle.setBackground(NoBackground.NO_BACKGROUND);
		defaultStyle.setAlignment(GraphicsContext.LEFT | GraphicsContext.VCENTER);
		stylesheet.setDefaultStyle(defaultStyle);

		// Default margin not added in the default style because it also applies for the composites.
		SimpleOutline defaultMargin = new SimpleOutline(6);

		TypeSelector labelTypeSelector = new TypeSelector(Label.class);
		TypeSelector checkboxTypeSelector = new TypeSelector(CheckBox.class);
		TypeSelector radioboxTypeSelector = new TypeSelector(RadioBox.class);
		TypeSelector switchboxTypeSelector = new TypeSelector(SwitchBox.class);
		TypeSelector pictocheckTypeSelector = new TypeSelector(PictoCheck.class);
		TypeSelector pictoradioTypeSelector = new TypeSelector(PictoRadio.class);
		TypeSelector pictoswitchTypeSelector = new TypeSelector(PictoSwitch.class);
		StateSelector stateCheckedSelector = new StateSelector(State.Checked);
		ClassSelector listItemSelector = new ClassSelector(ClassSelectors.LIST_ITEM);

		// Sets the label style.
		EditableStyle style = new EditableStyle();
		style.setPadding(defaultMargin);
		stylesheet.addRule(new AndCombinator(labelTypeSelector, listItemSelector), style);
		style.setBackground(NoBackground.NO_BACKGROUND);
		stylesheet.addRule(labelTypeSelector, style);

		// Sets the title style.
		style.clear();
		style.setFontProfile(new FontProfile(FontFamilies.ROBOTO, FontSize.LARGE, Font.STYLE_PLAIN));
		style.setBorderColor(TITLE_BORDER);
		ComplexRectangularBorder titleBorder = new ComplexRectangularBorder();
		titleBorder.setBottom(2);
		style.setBorder(titleBorder);
		stylesheet.addRule(new ClassSelector(ClassSelectors.TITLE), style);

		// Sets the list item style.
		style.clear();
		// ComplexRectangularBorder listItemBorder = new ComplexRectangularBorder();
		// listItemBorder.setBottom(1);
		// listItemBorder.setColorBottom(Colors.GRAY);
		// listItemStyle.setBorder(listItemBorder);
		style.setMargin(new ComplexOutline(0, 4, 0, 4));
		stylesheet.addRule(listItemSelector, style);

		style.clear();
		style.setBackground(new PlainBackground());
		style.setBackgroundColor(LIST_EVEN_BACKGROUND);
		stylesheet.addRule(new AndCombinator(listItemSelector, new EvenChildSelector()), style);

		// Sets the image style.
		style.clear();
		// Align with back button size.
		style.setPadding(new ComplexOutline(0, 0, 0, 5));
		stylesheet.addRule(new TypeSelector(Image.class), style);

		// Sets the unchecked toggle style.
		style.clear();
		style.setForegroundColor(UNCHECKED_FOREGROUND);
		style.setBorderColor(UNCHECKED_FOREGROUND);
		style.setMargin(defaultMargin);
		style.setAlignment(GraphicsContext.HCENTER | GraphicsContext.VCENTER);
		stylesheet.addRule(checkboxTypeSelector, style);
		stylesheet.addRule(radioboxTypeSelector, style);
		stylesheet.addRule(switchboxTypeSelector, style);

		style.clear();
		style.setBorder(new SimpleRectangularBorder(3));
		style.setPadding(new SimpleOutline(3));
		stylesheet.addRule(checkboxTypeSelector, style);

		style.clear();
		style.setBorder(new SimpleRoundedBorder(1000, 2));
		style.setPadding(new SimpleOutline(4));
		stylesheet.addRule(radioboxTypeSelector, style);

		style.clear();
		style.setBorder(new SimpleRoundedBorder(1000, 2));
		// style.setBackground(new SimpleRoundedPlainBackground(1000));
		style.setPadding(new ComplexOutline(4, 20, 4, 4));
		style.setAlignment(GraphicsContext.LEFT | GraphicsContext.VCENTER);
		stylesheet.addRule(switchboxTypeSelector, style);

		style.clear();
		style.setPadding(new ComplexOutline(4, 4, 4, 20));
		style.setAlignment(GraphicsContext.RIGHT | GraphicsContext.VCENTER);
		stylesheet.addRule(new AndCombinator(switchboxTypeSelector, stateCheckedSelector), style);

		// The font to use for the most of the picto widgets.
		FontProfile widgetPictoFontProfile = new FontProfile(FontFamilies.PICTO, FontSize.MEDIUM, Font.STYLE_PLAIN);

		// Sets the unchecked picto toggle style.
		style.clear();
		style.setFontProfile(widgetPictoFontProfile);
		style.setForegroundColor(UNCHECKED_FOREGROUND);
		style.setMargin(defaultMargin);
		stylesheet.addRule(pictocheckTypeSelector, style);
		stylesheet.addRule(pictoradioTypeSelector, style);
		stylesheet.addRule(pictoswitchTypeSelector, style);

		// Sets the widget and checked toggle style.
		style.clear();
		style.setMargin(defaultMargin);
		style.setForegroundColor(CHECKED_FOREGROUND);
		style.setBorderColor(CHECKED_FOREGROUND);
		stylesheet.addRule(new TypeSelector(ProgressBar.class), style);
		stylesheet.addRule(new TypeSelector(CircularProgressBar.class), style);
		stylesheet.addRule(new TypeSelector(Slider.class), style);
		stylesheet.addRule(new AndCombinator(checkboxTypeSelector, stateCheckedSelector), style);
		stylesheet.addRule(new AndCombinator(radioboxTypeSelector, stateCheckedSelector), style);
		stylesheet.addRule(new AndCombinator(switchboxTypeSelector, stateCheckedSelector), style);

		style.clear();
		style.setDimension(new FixedDimension(MWT.NONE, 10));
		style.setBackground(new PlainBackground());
		style.setBackgroundColor(ACTIVE_FOREGROUND);
		stylesheet.addRule(new TypeSelector(ProgressBar.class), style);

		// Sets the image widget style.
		style.clear();
		style.setMargin(defaultMargin);
		stylesheet.addRule(new TypeSelector(ImageSlider.class), style);
		stylesheet.addRule(new TypeSelector(ImageRadio.class), style);
		stylesheet.addRule(new TypeSelector(ImageCheck.class), style);
		stylesheet.addRule(new TypeSelector(ImageSwitch.class), style);

		// Sets the picto widget and checked picto toggle style.
		style.clear();
		style.setMargin(defaultMargin);
		style.setForegroundColor(CHECKED_FOREGROUND);
		style.setFontProfile(widgetPictoFontProfile);
		stylesheet.addRule(new TypeSelector(PictoSlider.class), style);
		stylesheet.addRule(new AndCombinator(pictocheckTypeSelector, stateCheckedSelector), style);
		stylesheet.addRule(new AndCombinator(pictoradioTypeSelector, stateCheckedSelector), style);
		stylesheet.addRule(new AndCombinator(pictoswitchTypeSelector, stateCheckedSelector), style);
		stylesheet.addRule(new TypeSelector(PictoProgress.class), style);

		// Sets the illustrated button style.
		ClassSelector illustratedButtonSelector = new ClassSelector(ClassSelectors.ILLUSTRATED_BUTTON);
		style.clear();
		style.setBackgroundColor(CHECKED_FOREGROUND);
		style.setMargin(new ComplexOutline(12, 60, 12, 60));
		// The content of the button is centered horizontally and vertically.
		style.setAlignment(GraphicsContext.HCENTER | GraphicsContext.VCENTER);
		stylesheet.addRule(illustratedButtonSelector, style);

		// Sets the illustrated active button style.
		style.clear();
		style.setBackgroundColor(ACTIVE_FOREGROUND);
		stylesheet.addRule(new AndCombinator(illustratedButtonSelector, new StateSelector(State.Active)), style);

		// Sets the text title style.
		style.clear();
		style.setBorderColor(TITLE_BORDER);
		ComplexRectangularBorder textTitleBorder = new ComplexRectangularBorder();
		textTitleBorder.setBottom(1);
		style.setBorder(textTitleBorder);
		stylesheet.addRule(new ClassSelector(ClassSelectors.TEXT_TITLE), style);

		// Sets the multiline style.
		style.clear();
		style.setTextManager(new ComplexTextManager(40));
		stylesheet.addRule(new ClassSelector(ClassSelectors.MULTILINE), style);
	}
}
