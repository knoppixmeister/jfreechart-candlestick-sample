import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.MarkerAxisBand;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class Test2 {
	public static void main(String[] args) {
		JFrame fr = new JFrame();

		OHLCSeriesCollection collection = new OHLCSeriesCollection();

		OHLCSeries series = new OHLCSeries("");
		series.addChangeListener(new SeriesChangeListener() {
			@Override
			public void seriesChanged(SeriesChangeEvent event) {
				double close = ((OHLCItem)series.getDataItem(series.getItemCount()-1)).getCloseValue();

				//System.out.println("SERIES_CHANGED: CL: "+close);
			}
		});

		collection.addSeries(series);

		/*
		collection.addChangeListener(new DatasetChangeListener() {
			@Override
			public void datasetChanged(DatasetChangeEvent event) {
				OHLCItem item = (OHLCItem)collection.getSeries(0).getDataItem(collection.getSeries(0).getItemCount()-1);

				if(item != null) {
					double val = item.getCloseValue();

					System.out.println("COLLECTION_CH_EVENT. CL_VAL: "+val);
				}
			}
		});
		*/

		long time = System.currentTimeMillis();

		/*
		for(int i=0; i<200; i++) {
			series.add(
				new OHLCItem(
					new FixedMillisecond(time+(i*1000)),
					5000,	//open,
					8000,	//high,
					4000,	//low,
					7000	//close
				)
			);
		}
		*/
		
		/*
		series.add(
			new OHLCItem(
				new FixedMillisecond(time+1000),
				4000,	//open,
				9000,	//high,
				2000,	//low,
				8000	//close
			)
		);
		*/

		DateAxis dateAxis = new DateAxis();
		dateAxis.setFixedAutoRange(90000);

		dateAxis.setPositiveArrowVisible(true);
		dateAxis.setNegativeArrowVisible(true);
		dateAxis.setUpArrow(new Rectangle2D.Double(10, 10, 20, 20));

		series.addChangeListener(new SeriesChangeListener() {
			@Override
			public void seriesChanged(SeriesChangeEvent event) {
				
				System.out.println(	"DA_AB: "+	new BigDecimal(dateAxis.getUpperBound()).longValue());
			}
		});

		NumberAxis priceAxis = new NumberAxis();
		//priceAxis.setLowerBound(3000);
		//priceAxis.setUpperBound(12000);

		//priceAxis.setAutoRangeStickyZero(false);
		priceAxis.setAutoRangeIncludesZero(false);
		//priceAxis.setRangeType(RangeType.POSITIVE);

		MarkerAxisBand markerAxisBand = new MarkerAxisBand(
			priceAxis,	//axis,
			0,		//topOuterGap,
			100,		//topInnerGap,
			100, 		//bottomOuterGap,
			100,		//bottomInnerGap,
			new Font("Sans Serif", Font.BOLD, 11)	//font
		);
		
		//markerAxisBand.addMarker(new IntervalMarker(1000, 9000));
		
		//priceAxis.setMarkerBand(markerAxisBand);

		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(3);
		candlestickRenderer.setDrawVolume(true);
		candlestickRenderer.setSeriesPaint(0, Color.YELLOW);
		
		
		candlestickRenderer.setDefaultOutlinePaint(Color.CYAN);
		
		candlestickRenderer.setUpPaint(ChartColor.DARK_GREEN);
		candlestickRenderer.setDownPaint(ChartColor.DARK_RED);
		
		candlestickRenderer.setDefaultFillPaint(Color.BLACK);
		
		candlestickRenderer.setSeriesOutlinePaint(0, Color.BLUE);
		candlestickRenderer.setUseOutlinePaint(false);
		
		XYPlot plot = new XYPlot(
			collection,		//dataset,
			dateAxis,		//domainAxis,
			priceAxis,		//rangeAxis,
			candlestickRenderer	//renderer
		);
		//plot.setBackgroundPaint(ChartColor.WHITE);
		

		plot.setNoDataMessage("Waiting for data ...");
		plot.setNoDataMessageFont(new Font("Sans Serif", Font.BOLD, 19));
		plot.setNoDataMessagePaint(ChartColor.DARK_RED);
		
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		plot.setRangeZeroBaselineVisible(true);
		plot.getRangeAxis().setLowerMargin(15);
		//plot.getDomainAxis().setFixedAutoRange(60000);
		
		plot.setDomainCrosshairVisible(true);
        plot.setDomainCrosshairLockedOnData(false);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairLockedOnData(false);
        
        plot.setRangePannable(true);
        plot.setDomainPannable(true);

		JFreeChart chart = new JFreeChart(plot);
							/*
							ChartFactory.createCandlestickChart(
								"",
								"",
								"",
								collection,
								false
							);
							*/
		//chart.getXYPlot().setDomainCrosshairVisible(true);
		//chart.getXYPlot().setDomainCrosshairLockedOnData(false);
		//chart.getXYPlot().setRangeCrosshairVisible(true);

		chart.removeLegend();
		//chart.setBackgroundPaint(ChartColor.WHITE);

		ChartPanel panel = new ChartPanel(chart);

		panel.setMouseWheelEnabled(true)
		.setMouseZoomable(true)
		.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println(e.getWheelRotation());
				
			}
		});
		chart.addProgressListener(new ChartProgressListener() {
			@Override
			public void chartProgress(ChartProgressEvent event) {
				//System.out.println("LLALALA");

				if(event.getType() != ChartProgressEvent.DRAWING_FINISHED) return;
				
				//System.out.println("LLL: "+	panel.getChart().getXYPlot().getDomainCrosshairValue()	);
			}
		});
		panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				//System.out.println("PRESSED");

				// plot.getDomainCrosshairValue();

				System.out.println(	"PPPP: "+panel.getChart().getXYPlot().getDomainCrosshairValue()	);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		//--- MA line ------------------------------------------------------------------------------------------------
		plot.setDataset(
			1,
			MovingAverage.createMovingAverage(
				collection,
				"-MAVG",
				3 * 24 * 60 * 60 * 1000L,
				0L
			)
		);
		plot.setRenderer(1, new StandardXYItemRenderer());
        //------------------------------------------------------------------------------------------------------------

		fr.setBounds(100, 10, 800, 500);

		fr.add(panel);

		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Random r = new Random();

				try {
					Thread.sleep(1000);
					
					series.setNotify(false);
					
					for(int i=0; i<200; i++) {
						series.add(
							new OHLCItem(
								new FixedMillisecond(time+(i*1000)),
								5000,	//open,
								8000,	//high,
								4000,	//low,
								7000,	//close,
								1000	//volume
							)
						);
					}
					
					series.setNotify(true);
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				
				while(true) {
					double val = r.nextInt(1600-1000)+3500;

					collection.getSeries(0).updatePrice(val);

					//((OHLCItem)series.getDataItem(0)).updatePrice(9000);

					//System.out.println("");
					//System.out.println("UP_VAl: "+val);
					
					try {
						Thread.sleep(1000);
					}
					catch(Exception e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
		
	}
}
