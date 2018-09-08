package com.fx.jfree.chart.utils;

import java.text.DecimalFormat;

public class MathUtils {
	public static final String TWO_DEC_DOUBLE_FORMAT = "##.00";

	public static double roundDouble(double value, String format) {
		DecimalFormat df = new DecimalFormat(format);

		return Double.valueOf(df.format(value));
	}
}
