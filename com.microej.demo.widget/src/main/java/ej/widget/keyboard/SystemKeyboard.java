/*
 * Java
 *
 * Copyright  2016-2019 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 * MicroEJ Corp. PROPRIETARY. Use is subject to license terms.
 */
package ej.widget.keyboard;

import ej.microui.event.generator.Keyboard;

/**
 * Represents a simple keyboard registered in the system pool
 */
public class SystemKeyboard extends Keyboard {

	/**
	 * Constructor
	 */
	public SystemKeyboard() {
		super(1);
		addToSystemPool();
	}
}
