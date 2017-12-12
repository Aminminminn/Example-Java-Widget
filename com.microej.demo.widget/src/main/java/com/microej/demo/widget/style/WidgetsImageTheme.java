/*
 * Java
 *
 * Copyright 2015-2017 IS2T. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found at http://www.is2t.com/open-source-bsd-license/.
 */
package com.microej.demo.widget.style;

import ej.widget.basic.image.ImageTheme;

/**
 * The image theme used in the demo.
 */
public class WidgetsImageTheme implements ImageTheme {

	@Override
	public String getCheckboxCheckedPath() {
		return "/images/checkbox_checked.png"; //$NON-NLS-1$
	}

	@Override
	public String getCheckboxUncheckedPath() {
		return "/images/checkbox_unchecked.png"; //$NON-NLS-1$
	}

	@Override
	public String getSwitchCheckedPath() {
		return "/images/switch_checked.png"; //$NON-NLS-1$
	}

	@Override
	public String getSwitchUncheckedPath() {
		return "/images/switch_unchecked.png"; //$NON-NLS-1$
	}

	@Override
	public String getRadioButtonCheckedPath() {
		return "/images/radio_checked.png"; //$NON-NLS-1$
	}

	@Override
	public String getRadioButtonUncheckedPath() {
		return "/images/radio_unchecked.png"; //$NON-NLS-1$
	}

	@Override
	public String getSliderHorizontalBarPath() {
		return "/images/slider_bar.png"; //$NON-NLS-1$
	}

	@Override
	public String getSliderVerticalBarPath() {
		throw new UnsupportedOperationException("Only horizontal bar."); //$NON-NLS-1$
	}

	@Override
	public String getSliderCursorPath() {
		return "/images/slider_cursor.png"; //$NON-NLS-1$
	}

	@Override
	public String getProgressBarHorizontalPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProgressBarVerticalPath() {
		throw new UnsupportedOperationException();
	}

}
