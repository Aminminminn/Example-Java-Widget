/*
 * Java
 *
 * Copyright 2017 IS2T. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found at http://www.is2t.com/open-source-bsd-license/.
 */
package com.microej.demo.widget.keyboard;

import ej.widget.keyboard.Layout;

/**
 * Represents a numeric layout
 */
public class NumericLayout implements Layout {

	@Override
	public String getFirstRow() {
		return "1234567890"; //$NON-NLS-1$
	}

	@Override
	public String getSecondRow() {
		return "-/:;()$&@\""; //$NON-NLS-1$
	}

	@Override
	public String getThirdRow() {
		return ".,?!\'§¤"; //$NON-NLS-1$
	}
}
