import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.ui.*;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;
import org.jfree.data.xy.XYDataset;

public class Test3 {
	public static void main(String[] args) {
		/*
		CrosshairOverlay chOverlay = new CrosshairOverlay();

		Crosshair crosshair1 = new Crosshair(0);
		crosshair1.setPaint(Color.BLACK);

		crosshair1.setLabelVisible(true);
		crosshair1.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		crosshair1.setLabelBackgroundPaint(Color.RED);

		Crosshair crosshair2 = new Crosshair(5000);
		crosshair1.setPaint(Color.BLACK);

		crosshair1.setLabelVisible(true);
		crosshair1.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		crosshair1.setLabelBackgroundPaint(Color.CYAN);

		chOverlay.addDomainCrosshair(crosshair1);
		chOverlay.addRangeCrosshair(crosshair2);
		*/

	//-----------------------------------------------------------

		OHLCSeriesCollection collection = new OHLCSeriesCollection();
		OHLCSeries series = new OHLCSeries("");
		collection.addSeries(series);

		TimeSeries series1 = new TimeSeries("");
		TimeSeriesCollection collection2 = new TimeSeriesCollection(series1);

		TimeSeries series2 = new TimeSeries("lines");
        TimeSeries series3 = new TimeSeries("lines");

        TimeSeriesCollection collection3 = new TimeSeriesCollection();
        collection3.addSeries(series2);
        collection3.addSeries(series3);

        for(int i=0; i<200; i++) {
			long tm = System.currentTimeMillis()+1000+i;

			series.add(new OHLCItem(
				new FixedMillisecond(tm),
				5000,	//open,
				9000,	//high,
				3000,	//low,
				7000	//close
			));

			series1.add(new TimeSeriesDataItem(new FixedMillisecond(tm), i % 2 == 0 ? 75 : 10));

			//series3.add(new TimeSeriesDataItem(new FixedMillisecond(tm), 75));
			//series2.add(new TimeSeriesDataItem(new FixedMillisecond(tm), 10));
		}

		DateAxis dateAxis = new DateAxis();
		NumberAxis priceAxis = new NumberAxis();

		XYPlot plot1 = new XYPlot(
			collection,		//dataset,
			null,			//domainAxis,
			priceAxis,		//rangeAxis,
			new CandlestickRenderer(
				3,		//candleWidth,
				true,	//drawVolume,
				null	//toolTipGenerator
			)//renderer
		);
		plot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		plot1.setRangePannable(true);

		plot1.setDomainCrosshairVisible(true);
		plot1.setRangeCrosshairVisible(true);
		plot1.setRangeCrosshairValue(4000);

		XYDataset dataset2 = MovingAverage.createMovingAverage(collection, "-MAVG", 3 * 24 * 60 * 60 * 1000L, 0L);
		plot1.setDataset(1, dataset2);
		plot1.setRenderer(1, new StandardXYItemRenderer());

		//------------------------------------------------------------
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		XYPlot plot2 = new XYPlot(
			collection2,								//dataset,
			null,										//domainAxis,
			new NumberAxis(),							//rangeAxis,
			//new XYLineAndShapeRenderer(true, false)	//renderer
			new StandardXYItemRenderer()
		);
		//plot2.setBackgroundPaint(ChartColor.DARK_RED);
		plot2.setRangeCrosshairVisible(true);
		plot2.setRangeCrosshairLockedOnData(false);
		plot2.setDomainCrosshairVisible(true);
		//plot2.setRangeZeroBaselineVisible(true);
		//plot2.setRangeGridlinePaint(new Color(142, 21, 153));

		plot2.getRenderer().setSeriesPaint(0, new Color(142, 21, 153), true);

		plot2.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		plot2.setRangePannable(true);

		/*
		XYDifferenceRenderer r = 	//new XYDifferenceRenderer(Color.RED, Color.LIGHT_GRAY, false);
									new XYDifferenceRenderer();
		//r.setRoundXCoordinates(true);
		plot2.setDataset(1, collection3);
		plot2.setRenderer(1, r);
		*/

		IntervalMarker target = new IntervalMarker(30.0, 70.0);
		//target.setLabel("Target Range");
		//target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
		//target.setLabelAnchor(RectangleAnchor.LEFT);
		//target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);

		plot2.addRangeMarker(target, Layer.BACKGROUND);

	//----------------------------------------------------------------------------------------------------------------------

		XYPlot plot3 = new XYPlot(
			//dataset,
			//domainAxis,
			//rangeAxis,
			//renderer
		);

		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(dateAxis);
		combinedPlot.setGap(-9);
		//combinedPlot.setInsets(new RectangleInsets(-20, -10, 0, -10));

		//combinedPlot.setDomainCrosshairVisible(true);
		
		combinedPlot.setDomainPannable(true);
		combinedPlot.setRangePannable(true);
		
		combinedPlot.add(plot1, 5);
		combinedPlot.add(plot2, 1);
		combinedPlot.add(plot3, 1);

		JFreeChart chart = new JFreeChart(combinedPlot);

		ChartPanel panel = new ChartPanel(chart);

		//panel.addOverlay(chOverlay);

		panel.setMouseWheelEnabled(true);
		panel.setMouseZoomable(false);

		JButton b = new JButton("BTN");
		b.setHorizontalAlignment(SwingConstants.RIGHT);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("MMMM");
			}
		});
		panel.add(b);

		panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("AAAAA");
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

		panel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				System.out.println("MOSE MOVED");

				//panel.getScreenDataArea(x, y);

				Rectangle2D dataArea = panel.getScreenDataArea(e.getX(), e.getY());

				if(dataArea != null) {
					System.out.println("X: "+e.getX());

					double x = dateAxis.java2DToValue(e.getX(), dataArea, combinedPlot.getDomainAxisEdge());
					//double y = priceAxis.java2DToValue(e.getY(), dataArea, plot1.getRangeAxisEdge());

					//System.out.println("X: "+x);

					//plot1.setRangeCrosshairValue(y);
					plot1.setDomainCrosshairValue(x);

					plot2.setDomainCrosshairVisible(true);
					plot2.setDomainCrosshairValue(x);

					plot3.setDomainCrosshairVisible(true);
					plot3.setDomainCrosshairValue(x);

					//combinedPlot.setDomainCrosshairValue(x);
				}
				else System.out.println("NO DATA AREA");
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				;
			}
		});
		panel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("MW: "+e.getWheelRotation()	);

				//((XYPlot)((CombinedDomainXYPlot)panel.getChart().getPlot()).getSubplots().get(0)).getDataR
			}
		});

		panel.getChart().removeLegend();

		JFrame fr = new JFrame();

		fr.setBounds(10, 10, 1300, 600);

		fr.add(panel);

		/*
		JButton button = new JButton("Click");
		button.setBounds(10, 10, 100, 30);
		fr.add(button);
		*/

		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);

		
		/*
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				combinedPlot.remove(plot3);
				
			}
		}).start();
		*/
	}
}