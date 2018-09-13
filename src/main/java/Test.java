import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.labels.CrosshairLabelGenerator;
import org.jfree.chart.labels.StandardCrosshairLabelGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Args;
import org.jfree.data.Range;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.json.impl.JSONArray;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import com.fx.jfree.chart.candlestick.CustomHighLowItemLabelGenerator;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

class DateDomainCrossharLabelGenerator implements CrosshairLabelGenerator {
	public String labelTextFormat;

	public DateDomainCrossharLabelGenerator() {
		this(" {0} ");
	}
	
	public DateDomainCrossharLabelGenerator(String labelTextFormat) {
		this.labelTextFormat = labelTextFormat;
	}
	
	@Override
	public String generateLabel(Crosshair crosshair) {
		Args.nullNotPermitted(crosshair, "crosshair");

		return	MessageFormat.format(
									labelTextFormat,
									DateTimeFormat.forPattern("dd-MM-YYYY HH:mm").print(new DateTime(Long.parseLong(((long)crosshair.getValue())+"")))
								);
	}
}

public class Test {
	public static double oldPrice = 0;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch(UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		JFrame fr = new JFrame("");

	//--------------------------------------------------------------------------------------------

		CrosshairOverlay overlay = new CrosshairOverlay();

		Crosshair domainCrosshair = new Crosshair(0);

		domainCrosshair.setPaint(Color.BLACK);
		domainCrosshair.setLabelVisible(true);
		domainCrosshair.setLabelPaint(Color.WHITE);
		domainCrosshair.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		domainCrosshair.setLabelBackgroundPaint(Color.decode("#4a4a4a"));

		domainCrosshair.setLabelGenerator(new DateDomainCrossharLabelGenerator());

		//((StandardCrosshairLabelGenerator)domainCrosshair.getLabelGenerator())

		overlay.addDomainCrosshair(domainCrosshair);

		//-------------------------------------------------------

		Crosshair crosshair2 = new Crosshair(0);
		overlay.addRangeCrosshair(crosshair2);

		crosshair2.setPaint(Color.GRAY);

		crosshair2.setLabelVisible(true);
		//crosshair2.setStroke(new BasicStroke(1));
		crosshair2.setLabelOutlineVisible(false);
		//crosshair2.setLabelOutlinePaint(ChartColor.DARK_MAGENTA);
		crosshair2.setLabelPaint(Color.WHITE);
		//crosshair2.setLabelXOffset(10);
		//crosshair2.setLabelYOffset(20);
		crosshair2.setLabelBackgroundPaint(Color.decode("#4a4a4a"));//new Color(255, 255, 0, 100)
		crosshair2.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		crosshair2.setLabelGenerator(new StandardCrosshairLabelGenerator(" {0} ", NumberFormat.getInstance()) 	);
		//crosshair2.setValue(6340);

	//---------------------------------------------------------------------------------------------------------------------

		OHLCSeries series = new OHLCSeries("");

		//series.add(new OHLCItem(new FixedMillisecond(System.currentTimeMillis()), 5000, 9000, 3000, 7000));
		//series.add(new OHLCItem(new FixedMillisecond(System.currentTimeMillis()+1000), 8000, 9000, 4600, 7000));

		//crosshair1.setValue(System.currentTimeMillis()+10000000);

		OHLCSeriesCollection collection = new OHLCSeriesCollection();

		collection.addChangeListener(new DatasetChangeListener() {
			@Override
			public void datasetChanged(DatasetChangeEvent event) {
				//System.out.println(event.toString());
			}
		});

		collection.addSeries(series);

		DateAxis dateAxis = new DateAxis();
		//dateAxis.setAxisLineVisible(false);
		//dateAxis.setAxisLinePaint(ChartColor.BLUE);
		
		NumberAxis priceAxis = new NumberAxis("");
		priceAxis.setAutoRangeIncludesZero(false);
		//priceAxis.setAxisLineVisible(true);
		//priceAxis.setAxisLinePaint(ChartColor.DARK_RED);
		//priceAxis.setVisible(false);

		//NumberAxis leftPriceAxis = new NumberAxis("");
		//leftPriceAxis.setAutoRangeIncludesZero(false);

		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(
			3,		//CandlestickRenderer.WIDTHMETHOD_AVERAGE,
			true,	
			//null
			new CustomHighLowItemLabelGenerator(
				new SimpleDateFormat("kk:mm"),
				new DecimalFormat("0.0")
			)
		);

		//new CustomHighLowItemLabelGenerator(new SimpleDateFormat("kk:mm"), new DecimalFormat("0.000"));

		candlestickRenderer.setUseOutlinePaint(false);
		candlestickRenderer.setUpPaint(Color.decode("#238853"));
		candlestickRenderer.setDownPaint(Color.decode("#dc5538"));
		//candlestickRenderer.setDefaultOutlinePaint(Color.CYAN);

		XYPlot plot = new XYPlot(
			collection,			//dataset,
			dateAxis,			//domainAxis,
			priceAxis,			//rangeAxis,
			candlestickRenderer	//renderer
		);
		plot.setAxisOffset(new RectangleInsets(-4, -7, 0, 0));//-8
		//plot.setAxisOffset(RectangleInsets.ZERO_INSETS);

		//plot.setRangeAxis(0, leftPriceAxis, true);
		//plot.setRangeAxis(0, priceAxis, true);

		/*
		plot.setDomainCrosshairVisible(true);
		plot.setDomainCrosshairPaint(ChartColor.GRAY);
		plot.setDomainCrosshairLockedOnData(true);

		plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairPaint(ChartColor.GRAY);
        plot.setRangeCrosshairLockedOnData(true);
        */

		//----------------------------------------------------------------------
		ValueMarker priceLine = new ValueMarker(0);
		//priceLine.setLabel(7000+"");
		priceLine.setPaint(ChartColor.DARK_RED);
		priceLine.setLabelFont(new Font("Sans Serial", Font.BOLD, 11));
		priceLine.setLabelPaint(ChartColor.WHITE);
		priceLine.setLabelBackgroundColor(Color.WHITE);
		priceLine.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        priceLine.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		plot.addRangeMarker(priceLine);
		//----------------------------------------------------------------------

		/*
		series.addChangeListener(new SeriesChangeListener() {
			@Override
			public void seriesChanged(SeriesChangeEvent event) {
				double val = ((OHLCItem)series.getDataItem(series.getItemCount()-1)).getCloseValue();

				System.out.println("SERIES CHANGED. VAL: "+val);
				
				fr.setTitle(	String.format("%.1f", val)	);
				
				priceLine.setValue(val);
				priceLine.setLabel(	String.format("%.1f", val)	);
			}
		});
		*/
		collection.addChangeListener(new DatasetChangeListener() {
			@Override
			public void datasetChanged(DatasetChangeEvent event) {
				if(collection.getSeriesCount() == 0) return;

				OHLCSeries ser = collection.getSeries(0);
				if(ser == null) return;

				double val = ((OHLCItem)ser.getDataItem(collection.getSeries(0).getItemCount()-1)).getYValue();

				if(val > oldPrice) {
					priceLine.setLabelBackgroundColor(Color.decode("#238853"));
					priceLine.setPaint(Color.decode("#238853"));
				}
				else {
					priceLine.setLabelBackgroundColor(Color.decode("#dc5538"));
					priceLine.setPaint(Color.decode("#dc5538"));
				}

				oldPrice = val;

				//System.out.println("DS_CH_LISTENER ....VAl: "+val);

				fr.setTitle(	String.format("%.1f", val)	);

				priceLine.setValue(val);
				priceLine.setLabel(" "+	String.format("%.1f", val) + " ");
			}
		});

		//----------------------------------------------------------------------
		/*
		XYPointerAnnotation pointer = new XYPointerAnnotation("Best Bid ___", System.currentTimeMillis()+100, 6000, 3.0 * Math.PI / 4.0);
        pointer.setBaseRadius(150.0);
        pointer.setTipRadius(0.0);
        pointer.setFont(new Font("SansSerif", Font.PLAIN, 19));
        pointer.setPaint(Color.RED);
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
        plot.addAnnotation(pointer);
        */
		//----------------------------------------------------------------------


        //----------------------------------------------------------------------
		/*
        Marker timeVerticalLine = new ValueMarker(System.currentTimeMillis()+100);
        timeVerticalLine.setPaint(ChartColor.DARK_CYAN);
        timeVerticalLine.setLabel("LALAL");
        timeVerticalLine.setLabelBackgroundColor(ChartColor.GREEN);
        timeVerticalLine.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        timeVerticalLine.setLabelTextAnchor(TextAnchor.BASELINE_CENTER);
        plot.addDomainMarker(timeVerticalLine);
        */

		//----------------------------------------------------------------------

		plot.setRangePannable(true);
		plot.setDomainPannable(true);
		
		plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);

		plot.setNoDataMessage("Waiting for data ...");
		plot.setNoDataMessagePaint(ChartColor.DARK_RED);
		plot.setNoDataMessageFont(new Font("Sans Serif", Font.BOLD, 19));

		//-----------------------------------------------------------------------
		/*
		NumberAxis rangeAxis2 = new NumberAxis();
		rangeAxis2.setPositiveArrowVisible(true);
		
		XYPlot plot2 = new XYPlot(
			null,//dataset,
			null,//domainAxis,
			rangeAxis2,//rangeAxis,
			null//renderer
		);
		plot2.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		plot2.setAxisOffset(RectangleInsets.ZERO_INSETS);
		*/
		//-----------------------------------------------------------------------

		/*
		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(dateAxis);
		
		combinedPlot.setAxisOffset(RectangleInsets.ZERO_INSETS);
		combinedPlot.setGap(-8);
		
		combinedPlot.add(plot, 5);
		//combinedPlot.add(plot2, 1);
		
		combinedPlot.setRangePannable(true);
		combinedPlot.setDomainPannable(true);
		*/

		//--------------------------------------------------------------------------------------
		
		JFreeChart chart = new JFreeChart(plot);
		chart.addProgressListener(new ChartProgressListener() {
			@Override
			public void chartProgress(ChartProgressEvent event) {
				if(event.getPercent() < 100) {
					crosshair2.setVisible(false);
				}
				else crosshair2.setVisible(true);

				System.out.println("LOAD_PERC: "+event.getPercent());
			}
		});

		chart.removeLegend();

		ChartPanel panel = new ChartPanel(chart);
		
		panel.addOverlay(overlay);

		panel.setRegularCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		//panel.setMinimumDrawWidth(0);
		//panel.setMinimumDrawHeight(0);

		panel.setMouseZoomable(true);
		panel.setMouseWheelEnabled(true);

		JButton b = new JButton("Btn");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("BTN_CLK");
			}
		});
		panel.add(b);

		/*
		panel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				//System.out.println(		e.getWheelRotation()	);	
			}
		});
		 */
		
		panel.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				//System.out.println("AAA. X:"+event.getTrigger().getX()+" - Y:"+event.getTrigger().getY());

				Rectangle2D rect2d = panel.getScreenDataArea();

				double percentX = event.getTrigger().getX()*100/rect2d.getWidth();

				ValueAxis domainAxis = plot.getDomainAxis();
		        Range range = domainAxis.getRange();

				double c = domainAxis.getLowerBound() + (percentX / 100.0) * range.getLength();

				if(domainCrosshair.isVisible()) domainCrosshair.setValue(c);

				//System.out.println("C: "+c);

				//plot.setDomainCrosshairValue(Double.valueOf(c));

				double percentY = 100 - (event.getTrigger().getY()*100/rect2d.getHeight());
				
				ValueAxis priceAxis = plot.getRangeAxis();
				Range range2 = priceAxis.getRange();
				
				c = (priceAxis.getLowerBound() + (percentY / 100.0) * range2.getLength());
				
				crosshair2.setValue(c);
				
				//plot.setRangeCrosshairValue(c);
				
				//panel.repaint();
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
			}
		});

		//fr.setExtendedState(JFrame.MAXIMIZED_BOTH);

		fr.setBounds(200, 10, 1000, 500);

		fr.add(panel);

		//crosshair1.setValue(5000);

        //ValueAxis domainAxis = plot.getDomainAxis();
        //Range range = domainAxis.getRange();

        //System.out.println(domainAxis.getLowerBound()+" : "+range.getLength());

        //double c = domainAxis.getLowerBound() + (20.0 / 100.0) * range.getLength();

        //System.out.println("C: "+c);

        //plot.setDomainCrosshairValue(Double.valueOf(c));

        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);

		
		
		
		
		/*
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
					}
					catch (Exception e) {
					}

					OHLCSeriesCollection coll = (OHLCSeriesCollection)panel.getChart().getXYPlot().getDataset(0);
					coll.getSeries(0).add(
						new OHLCItem(
							new FixedMillisecond(System.currentTimeMillis()+1000),
							3000,
							11000,
							4600,
							7000
						)
					);
					
					/*
					series.add(
						new OHLCItem(
							new FixedMillisecond(System.currentTimeMillis()+1000),
							3000,
							11000,
							4600,
							7000
						)
					);
					*

					//System.out.println("ADDED. CNT: "+series.getItemCount());
				}
			}
		}).start();
		*/

	//--------------------------------------------------------------------------------------------------------------

		OkHttpClient client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS)
														.retryOnConnectionFailure(true)
														.build();

		//https://min-api.cryptocompare.com/data/histoday?fsym=BTC&tsym=USD&limit=10000
		//https://min-api.cryptocompare.com/data/histohour?fsym=BTC&tsym=USD&limit=10000

		client.newCall(new Request.Builder().url("https://min-api.cryptocompare.com/data/histohour?fsym=BTC&tsym=USD&limit=10000").build()).enqueue(new Callback() {
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String json = response.body().string();

				Moshi moshi = new Moshi.Builder().build();

				JsonAdapter<CCResponse> jsonAdapter = moshi.adapter(CCResponse.class);
				CCResponse resp = jsonAdapter.fromJson(json);

				System.out.println(json);
				System.out.println("");

				System.out.println(	"Len: "+resp.Data.get(0).close	);

				OHLCSeries ser = new OHLCSeries("");
				for(int i=0; i<resp.Data.size(); i++) {
					OHLCItem ohlcItem = new OHLCItem(
						new FixedMillisecond(resp.Data.get(i).time),
						resp.Data.get(i).open,	//open,
						resp.Data.get(i).high,	//high,
						resp.Data.get(i).low,	//low,
						resp.Data.get(i).close	//close
					);

					ser.add(ohlcItem);
				}

				if(ser.getItemCount() > 0) {
					collection.removeAllSeries();
					collection.addSeries(ser);
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}
		});

		/*
		client.newWebSocket(
			new Request.Builder().url("wss://api.bitfinex.com/ws/2")
								//.url("wss://stream.binance.com:9443/stream?streams=ethbtc@kline_5m")
								.build(),
			new BFWebSocketListener(collection)
			//new BNWebSocketListener()
		);
		*/

		//---------------------------------------------------------------------------------------------------------------
	}
}

class BNWebSocketListener extends WebSocketListener {
	@Override
	public void onOpen(WebSocket socket, Response response) {
		System.out.println("BN_ON_OPEN");
	}

	@Override
	public void onMessage(WebSocket socket, String text) {
		System.out.println("BN_ON_MESSAGE. "+text);
	}
}
