/*
 * Java
 *
 * Copyright 2016 IS2T. All rights reserved.
 * IS2T PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package ej.widget.chart.scale;

/**
 * Represents a chart scale where the top value is set to a fixed value
 */
public class FixedChartScale extends ChartScale {

	/**
	 * The top value of the scale
	 */
	private final float topValue;

	/**
	 * Constructor
	 * 
	 * @param numValues
	 *            the number of values to show on the scale
	 * @param topValue
	 *            the value to set at the top of the scale
	 */
	public FixedChartScale(int numValues, float topValue) {
		super(numValues);
		this.topValue = topValue;
	}

	/**
	 * Gets the top value of the scale This implementation simply returns the top value given in the constructor
	 * 
	 * @return the top value of the scale
	 */
	@Override
	public float getTopValue() {
		return this.topValue;
	}
}
