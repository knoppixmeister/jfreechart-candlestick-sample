import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.InvalidObjectException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
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

import com.fx.jfree.chart.candlestick.CustomHighLowItemLabelGenerator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Test {
	public static double oldPrice = 0;

	public static void main(String[] args) {
		JFrame fr = new JFrame("");
		
	//--------------------------------------------------------------------------------------------
		CrosshairOverlay overlay = new CrosshairOverlay();

		Crosshair crosshair1 = new Crosshair(0);
		crosshair1.setPaint(Color.BLACK);

		crosshair1.setLabelVisible(true);
		crosshair1.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		crosshair1.setLabelBackgroundPaint(Color.RED);

		overlay.addDomainCrosshair(crosshair1);

		//-------------------------------------------------------

		//SlidingCategoryDataset slidingDataset = new SlidingCategoryDataset(null, 0, 100);
		
		Crosshair crosshair2 = new Crosshair(0);
		crosshair2.setPaint(Color.GRAY);

		overlay.addRangeCrosshair(crosshair2);

		crosshair2.setLabelVisible(true);
		crosshair2.setStroke(new BasicStroke(1));
		crosshair2.setLabelOutlineVisible(true);
		crosshair2.setLabelOutlinePaint(ChartColor.DARK_MAGENTA);
		crosshair2.setLabelXOffset(10);
		crosshair2.setLabelYOffset(20);
		crosshair2.setLabelBackgroundPaint(Color.CYAN);//new Color(255, 255, 0, 100)
		crosshair2.setLabelAnchor(RectangleAnchor.TOP_RIGHT);

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
		NumberAxis priceAxis = new NumberAxis("");
		priceAxis.setAutoRangeIncludesZero(false);
		//priceAxis.setVisible(false);

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

		plot.setDomainCrosshairVisible(true);
		plot.setDomainCrosshairLockedOnData(true);

        //plot.setRangeCrosshairVisible(false);
        //plot.setRangeCrosshairLockedOnData(true);

		//----------------------------------------------------------------------
		ValueMarker priceLine = new ValueMarker(6200);
		priceLine.setLabel(7000+"");
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
				double val = ((OHLCItem)collection.getSeries(0).getDataItem(collection.getSeries(0).getItemCount()-1)).getYValue();
				
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
				priceLine.setLabel(" "+	String.format("%.1f", val) +" "	);
			}
		});
		
		//----------------------------------------------------------------------
		XYPointerAnnotation pointer = new XYPointerAnnotation("Best Bid ___", System.currentTimeMillis()+100, 6000, 3.0 * Math.PI / 4.0);
        pointer.setBaseRadius(150.0);
        pointer.setTipRadius(0.0);
        pointer.setFont(new Font("SansSerif", Font.PLAIN, 19));
        pointer.setPaint(Color.RED);
        pointer.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
        plot.addAnnotation(pointer);
		//----------------------------------------------------------------------
		
        
        //----------------------------------------------------------------------
        Marker timeVerticalLine = new ValueMarker(System.currentTimeMillis()+100);
        timeVerticalLine.setPaint(ChartColor.DARK_CYAN);
        timeVerticalLine.setLabel("LALAL");
        timeVerticalLine.setLabelBackgroundColor(ChartColor.GREEN);
        timeVerticalLine.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
        timeVerticalLine.setLabelTextAnchor(TextAnchor.BASELINE_CENTER);
        plot.addDomainMarker(timeVerticalLine);
        //----------------------------------------------------------------------
        
		plot.setRangePannable(true);
		plot.setDomainPannable(true);
		plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);
		
		JFreeChart chart = new JFreeChart(plot);
	
		chart.removeLegend();

		ChartPanel panel = new ChartPanel(chart);

		panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		panel.setMouseZoomable(true);
		panel.setMouseWheelEnabled(true);


		panel.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println(		e.getWheelRotation()	);
				
			}
		});
		
		panel.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				//System.out.println("AAA. X:"+event.getTrigger().getX()+" - Y:"+event.getTrigger().getY());

				//crosshair2.setValue(5000);
				
				//System.out.println(	fr.getWidth()	);
				
				/*
				 1300   300
				 ---- = -----
				 100     x
				 */
				
				//System.out.println(	event.getTrigger().getX()*100/fr.getWidth() + "%"	);
				
				double percentX = event.getTrigger().getX()*100/fr.getWidth();
				
				ValueAxis domainAxis = plot.getDomainAxis();
		        Range range = domainAxis.getRange();

				double c = domainAxis.getLowerBound() + (percentX / 100.0) * range.getLength();

				//System.out.println("C: "+c);

				plot.setDomainCrosshairValue(Double.valueOf(c));

				double percentY = 100 - (event.getTrigger().getY()*100/(fr.getHeight()-100));
				
				ValueAxis priceAxis = plot.getRangeAxis();
				Range range2 = priceAxis.getRange();
				
				c = (priceAxis.getLowerBound() + (percentY / 100.0) * range2.getLength());
				
				crosshair2.setValue(c);
				
				//panel.repaint();
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
			}
		});

		panel.addOverlay(overlay);

		
		//fr.setExtendedState(JFrame.MAXIMIZED_BOTH);

		fr.setBounds(200, 10, 1000, 500);

		fr.add(panel);

		//crosshair1.setValue(5000);

        ValueAxis domainAxis = plot.getDomainAxis();
        Range range = domainAxis.getRange();

        //System.out.println(domainAxis.getLowerBound()+" : "+range.getLength());

        double c = domainAxis.getLowerBound() + (20 / 100.0) * range.getLength();

        //System.out.println("C: "+c);

        plot.setDomainCrosshairValue(Double.valueOf(c));

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
														.build();

		client.newWebSocket(
			new Request.Builder().url("wss://api.bitfinex.com/ws/2")
								//.url("wss://stream.binance.com:9443/stream?streams=ethbtc@kline_5m")
								.build(),
			new BFWebSocketListener(collection)
			//new BNWebSocketListener()
		);

		//---------------------------------------------------------------------------------------------------------------
	}
}

class BFWebSocketListener extends WebSocketListener {
	private OHLCSeriesCollection collection;

	private org.json.JSONArray	arr = null,
								arr2 = null,
								arr3 = null;

	public BFWebSocketListener(OHLCSeriesCollection collection) {
		this.collection = collection;
	}

	@Override
	public void onOpen(WebSocket socket, Response response) {
		System.out.println("BF_ON_OPEN");

		socket.send("{\"event\":\"subscribe\",\"channel\":\"candles\",\"key\":\"trade:1m:tBTCUSD\"}");
	}

	@Override
	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		t.printStackTrace();
	}

	@Override
	public void onClosing(WebSocket webSocket, int code, String reason) {
		System.out.println("BF_SOCKET_CLOSING. CODE: "+code+" "+reason);
	}

	@Override
	public void onClosed(WebSocket webSocket, int code, String reason) {
		System.out.println("BF_SOCKET_CLOSED. CODE: "+code+" "+reason);
	}

	@Override
	public void onMessage(WebSocket socket, String text) {
		System.out.println("BF_ON_MESSAGE. "+text);

		if(text.contains("event") || text.contains("hb")) return;
		
		arr = new org.json.JSONArray(text);

		if(arr.get(1) == null) return;

		arr2 = new org.json.JSONArray(arr.get(1).toString().trim());

		//System.out.println(	arr2.length()	);

		if(arr2.length() > 6) {//add first data
			//OHLCSeriesCollection col = new OHLCSeriesCollection();
			OHLCSeries ser = new OHLCSeries("");
			//col.addSeries(ser);
			
			for(int i=0; i<arr2.length(); i++) {
				arr3 = new org.json.JSONArray(arr2.get(i).toString().trim());

				OHLCItem ohlcItem = new OHLCItem(
					new FixedMillisecond(Long.parseLong(arr3.get(0).toString().trim())),
					Double.parseDouble(arr3.get(1).toString().trim()),	//open,
					Double.parseDouble(arr3.get(3).toString().trim()),	//high,
					Double.parseDouble(arr3.get(4).toString().trim()),	//low,
					Double.parseDouble(arr3.get(2).toString().trim()),	//close
					Double.parseDouble(arr3.get(5).toString().trim())	//volume
				);

				collection.getSeries(0).add(ohlcItem);
				
				//ser.add(ohlcItem);
			}
			
			/*
			if(ser.getItemCount() > 0) {
				collection.removeAllSeries();
				collection.addSeries(ser);
			}
			*/
		}
		else {//update last data
			System.out.println("6_items: "+	arr2.toString().trim()	);
			
			arr3 = new org.json.JSONArray(arr2.toString().trim());
			
			long newTime 		= Long.parseLong(arr3.get(0).toString().trim());
			long lastOhlcTime 	= ((OHLCItem)collection.getSeries(0).getDataItem(collection.getSeries(0).getItemCount()-1)).getPeriod().getFirstMillisecond();
			
			if(newTime > lastOhlcTime) {//add new candle data
				OHLCItem ohlcItem = new OHLCItem(
					new FixedMillisecond(newTime),
					Double.parseDouble(arr3.get(1).toString().trim()),	//open,
					Double.parseDouble(arr3.get(3).toString().trim()),	//high,
					Double.parseDouble(arr3.get(4).toString().trim()),	//low,
					Double.parseDouble(arr3.get(2).toString().trim()),	//close
					Double.parseDouble(arr3.get(5).toString().trim())	//volume
				);
				
				collection.getSeries(0).add(ohlcItem);
			}
			else {//update last candle
				double newPrice = Double.parseDouble(arr3.get(2).toString().trim());

				System.out.println("STOCK_NEW_PR: "+newPrice);

				collection.getSeries(0).updatePrice(newPrice);

				/*
				collection.getSeries(0).remove(
					collection.getSeries(0).getItemCount()-1
				);

				OHLCItem ohlcItem = new OHLCItem(
					new FixedMillisecond(newTime),
					Double.parseDouble(arr3.get(1).toString().trim()),	//open,
					Double.parseDouble(arr3.get(3).toString().trim()),	//high,
					Double.parseDouble(arr3.get(4).toString().trim()),	//low,
					Double.parseDouble(arr3.get(2).toString().trim()),	//close
					Double.parseDouble(arr3.get(5).toString().trim())	//volume
				);

				collection.getSeries(0).add(ohlcItem);
				*/
			}

			//System.out.println(	"ITEMS_IN_SERIES: "		);
		}
		

		System.out.println("");
		
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
