package com.fx.jfree.chart.candlestick;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import com.fx.jfree.chart.model.Trade;
import com.fx.jfree.chart.utils.MathUtils;
import com.fx.jfree.chart.utils.TimeUtils;

@SuppressWarnings("serial")
public class JfreeCandlestickChart extends JPanel {
	private static final DateFormat READABLE_TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");

	private OHLCSeries ohlcSeries;
	private TimeSeries volumeSeries;

	private static final int MIN = 60000;
	// Every minute
	private int timeInterval = 1;
	private Trade candelChartIntervalFirstPrint = null;
	private double open = 0.0;
	private double close = 0.0;
	private double low = 0.0;
	private double high = 0.0;
	private long volume = 0;

	public JfreeCandlestickChart(String title) {
		final JFreeChart candlestickChart = createChart(title);
		final ChartPanel chartPanel = new ChartPanel(candlestickChart);

		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));

		chartPanel.setMouseZoomable(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setDoubleBuffered(true);

		add(chartPanel, BorderLayout.CENTER);
	}

	private JFreeChart createChart(String chartTitle) {
		/**
		 * Creating candlestick subplot
		 */
		// Create OHLCSeriesCollection as a price dataset for candlestick chart
		OHLCSeriesCollection candlestickDataset = new OHLCSeriesCollection();
		ohlcSeries = new OHLCSeries("Price");
		candlestickDataset.addSeries(ohlcSeries);
		
		// Create candlestick chart priceAxis
		NumberAxis priceAxis = new NumberAxis("");
		priceAxis.setAutoRangeIncludesZero(false);
		
		// Create candlestick chart renderer
		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(
			CandlestickRenderer.WIDTHMETHOD_AVERAGE,
			false,
			null
			//new CustomHighLowItemLabelGenerator(new SimpleDateFormat("kk:mm"), new DecimalFormat("0.000"))
		);
		// Create candlestickSubplot
		XYPlot candlestickSubplot = new XYPlot(candlestickDataset, null, priceAxis, candlestickRenderer);
		candlestickSubplot.setBackgroundPaint(Color.WHITE);
		
		candlestickSubplot.setDomainCrosshairVisible(true);
		candlestickSubplot.setDomainCrosshairLockedOnData(false);

		/**
		 * Creating volume subplot
		 */
		// creates TimeSeriesCollection as a volume dataset for volume chart
		TimeSeriesCollection volumeDataset = new TimeSeriesCollection();
		volumeSeries = new TimeSeries("");
		volumeDataset.addSeries(volumeSeries);
		// Create volume chart volumeAxis
		NumberAxis volumeAxis = new NumberAxis("");
		volumeAxis.setAutoRangeIncludesZero(false);
		
		// Set to no decimal
		volumeAxis.setNumberFormatOverride(new DecimalFormat("0"));
		
		// Create volume chart renderer
		XYBarRenderer timeRenderer = new XYBarRenderer();
		//timeRenderer.setMargin(-3);
		timeRenderer.setShadowVisible(false);
		timeRenderer.setDefaultToolTipGenerator(
			new StandardXYToolTipGenerator("Volume--> Time={1} Size={2}", new SimpleDateFormat("kk:mm"), new DecimalFormat("0"))
		);


		// Create volumeSubplot
		XYPlot volumeSubplot = new XYPlot(
			volumeDataset,
			null,
			volumeAxis,
			timeRenderer
		);
		//volumeSubplot.setBackgroundPaint(Color.WHITE);
		
		volumeSubplot.setDomainCrosshairVisible(true);
		volumeSubplot.setDomainCrosshairLockedOnData(false);
		
		XYPlot rsiSubPlot = new XYPlot(
			null,
			null,
			null,
			null
		);

		/**
		 * Create chart main plot with two subplots (candlestickSubplot,
		 * volumeSubplot) and one common dateAxis
		 */
		// Creating charts common dateAxis
		DateAxis dateAxis = new DateAxis("");
		dateAxis.setDateFormatOverride(new SimpleDateFormat("kk:mm"));
		// reduce the default left/right margin from 0.05 to 0.02
		dateAxis.setLowerMargin(0.02);
		dateAxis.setUpperMargin(0.02);

		// Create mainPlot
		CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(dateAxis);

		mainPlot.setGap(0.0);
		mainPlot.add(candlestickSubplot, 5);
		mainPlot.add(volumeSubplot, 1);
		mainPlot.add(rsiSubPlot, 1);
		mainPlot.setDomainPannable(true);
		mainPlot.setRangePannable(true);

		mainPlot.setDomainCrosshairVisible(true);
		mainPlot.setDomainCrosshairLockedOnData(false);
		
		//mainPlot.setOrientation(PlotOrientation.VERTICAL);

		JFreeChart chart = new JFreeChart(
			chartTitle,
			JFreeChart.DEFAULT_TITLE_FONT,
			mainPlot,
			true
		);
		chart.removeLegend();

		return chart;
	}

	public void addCandle(long time, double o, double h, double l, double c, long v) {
		try {
			// Add bar to the data. Let's repeat the same bar
			FixedMillisecond t = new FixedMillisecond(READABLE_TIME_FORMAT.parse(TimeUtils.convertToReadableTime(time)));
			ohlcSeries.add(t, o, h, l, c);
			volumeSeries.add(t, v);
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
	}

	public void onTrade(Trade t) {
		double price = t.getPrice();

		if(candelChartIntervalFirstPrint != null) {
			long time = t.getTime();

			if(timeInterval == (int) ((time / MIN) - (candelChartIntervalFirstPrint.getTime() / MIN))) {
				// Set the period close price
				close = price;
						//MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
				// Add new candle
				addCandle(time, open, high, low, close, volume);
				// Reset the intervalFirstPrint to null
				candelChartIntervalFirstPrint = null;
			}
			else {
				// Set the current low price
				/*
				if (MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT) < low)
					low = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
				*/
				
				if(price < low) low = price;

				// Set the current high price
				/*
				if (MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT) > high)
					high = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
				*/
				
				if(price > high) high = price;

				volume += t.getSize();
			}
		}
		else {
			// Set intervalFirstPrint
			candelChartIntervalFirstPrint = t;
			// the first trade price in the day (day open price)
			open = 	price;
					//MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);

			// the interval low
			low =	price;
					//MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
					
			// the interval high
			high =	price;
					//MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
			// set the initial volume
			volume = t.getSize();
		}
	}
}