/*
 * Java
 *
 * Copyright 2017 IS2T. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found at http://www.is2t.com/open-source-bsd-license/.
 */
package ej.widget.carousel;

/**
 * Represents a listener for carousel events
 */
public interface CarouselListener {

	/**
	 * Signals the carousel entries order has changed
	 *
	 * @param recipeIds
	 *            the ordered list of recipe ids
	 */
	public abstract void onCarouselChanged(int[] recipeIds);
}
