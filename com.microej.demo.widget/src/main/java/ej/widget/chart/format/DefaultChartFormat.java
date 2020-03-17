/*
 * Copyright 2017-2020 MicroEJ Corp. All rights reserved.
 * This library is provided in source code for use, modification and test, subject to license terms.
 * Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
 */
package ej.widget.chart.format;

/**
 * Default implementation of chart format.
 */
public class DefaultChartFormat implements ChartFormat {

	@Override
	public String formatShort(float value) {
		return Float.toString(value);
	}

	@Override
	public String formatLong(float value) {
		return Float.toString(value);
	}

}
