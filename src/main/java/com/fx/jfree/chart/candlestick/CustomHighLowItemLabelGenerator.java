package com.fx.jfree.chart.candlestick;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

/**
 * @author ashraf
 *
 */
@SuppressWarnings("serial")
public class CustomHighLowItemLabelGenerator extends HighLowItemLabelGenerator {
	private DateFormat dateFormatter;

	/** The number formatter. */
	private NumberFormat numberFormatter;

	/**
	 * Creates a tool tip generator using the supplied date formatter.
	 *
	 * @param dateFormatter
	 *            the date formatter (<code>null</code> not permitted).
	 * @param numberFormatter
	 *            the number formatter (<code>null</code> not permitted).
	 */
	public CustomHighLowItemLabelGenerator(DateFormat dateFormatter, NumberFormat numberFormatter) {
		if(dateFormatter == null) throw new IllegalArgumentException("Null 'dateFormatter' argument.");

		if(numberFormatter == null) {
			throw new IllegalArgumentException("Null 'numberFormatter' argument.");
		}
		
		this.dateFormatter = dateFormatter;
		this.numberFormatter = numberFormatter;
	}

	/**
	 * Generates a tooltip text item for a particular item within a series.
	 *
	 * @param dataset
	 *            the dataset.
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item (zero-based index).
	 *
	 * @return The tooltip text.
	 */
	@Override
	public String generateToolTip(XYDataset dataset, int series, int item) {
		String result = null;

		if(dataset instanceof OHLCDataset) {
			OHLCDataset d = (OHLCDataset) dataset;
			Number high = d.getHigh(series, item);
			Number low = d.getLow(series, item);
			Number open = d.getOpen(series, item);
			Number close = d.getClose(series, item);
			Number x = d.getX(series, item);

			result = d.getSeriesKey(series).toString();

			if (x != null) {
				Date date = new Date(x.longValue());
				
				result = result + "Time=" + this.dateFormatter.format(date);
				
				if (high != null) {
					result = result + ". High=" + numberFormatter.format(high.doubleValue());
				}
				if (low != null) {
					result = result + ". Low=" + this.numberFormatter.format(low.doubleValue());
				}
				if (open != null) {
					result = result + ". Open=" + this.numberFormatter.format(open.doubleValue());
				}
				if (close != null) {
					result = result + ". Close=" + this.numberFormatter.format(close.doubleValue());
				}
			}
		}

		return result;
	}
}
