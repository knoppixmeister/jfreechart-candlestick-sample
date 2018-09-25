import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.ui.*;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.time.*;
import org.jfree.data.time.ohlc.*;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Test3 {
	private static WebSocket socket;
	private static OkHttpClient client;
	private static OHLCSeriesCollection collection = new OHLCSeriesCollection();

	private static Queue<Long> dataFetchTimes = new ConcurrentLinkedQueue<>();
	
	public static void main(String[] args) {
		socket = null;

		/*
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
		*/
		
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

	//--------------------------------------------------------------------------------------------

		OHLCSeries series = new OHLCSeries("");
		collection.addSeries(series);

		TimeSeries series1 = new TimeSeries("");
		TimeSeriesCollection collection2 = new TimeSeriesCollection(series1);

		TimeSeries series2 = new TimeSeries("lines");
        TimeSeries series3 = new TimeSeries("lines");

        TimeSeriesCollection collection3 = new TimeSeriesCollection();
        collection3.addSeries(series2);
        collection3.addSeries(series3);

        series.addChangeListener(new SeriesChangeListener() {
			@Override
			public void seriesChanged(SeriesChangeEvent event) {
				//System.out.println(	event.getSource().toString()	);

				series1.setNotify(false);
				
				Map<Long, Double> rsiRes = RSI.run(series, 14);
				for(Map.Entry<Long, Double> entry : rsiRes.entrySet()) {
					//TimeSeriesDataItem tsdi = series1.getDataItem(new FixedMillisecond(entry.getKey()));
					//if(tsdi != null) {
						series1.addOrUpdate(new TimeSeriesDataItem(new FixedMillisecond(entry.getKey()), entry.getValue()));
					//}
				}
				
				series1.setNotify(true);
			}
        });

        /*
        for(int i=0; i<200; i++) {
        	long tm = System.currentTimeMillis()+1000+i;

			/*
			series.add(new OHLCItem(
				new FixedMillisecond(tm),
				5000,	//open,
				9000,	//high,
				3000,	//low,
				7000	//close
			));
			*

			//series1.add(new TimeSeriesDataItem(new FixedMillisecond(tm), i % 2 == 0 ? 75 : 10));

			//series3.add(new TimeSeriesDataItem(new FixedMillisecond(tm), 75));
			//series2.add(new TimeSeriesDataItem(new FixedMillisecond(tm), 10));
        }
		*/

        DateAxis dateAxis 		= new DateAxis();
        NumberAxis priceAxis 	= new NumberAxis();
        priceAxis.setAutoRangeIncludesZero(false);

        CandlestickRenderer renderer = new CandlestickRenderer(
			-1,	//candleWidth,
			true,										//drawVolume,
			null										//toolTipGenerator
		);
        renderer.setVolumePaint(ChartColor.BLACK);
       
        
        
		XYPlot plot1 = new XYPlot(
			collection,		//dataset,
			null,			//domainAxis,
			priceAxis,		//rangeAxis,
			renderer		//renderer
		);
		
		
		plot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		plot1.setRangePannable(true);
		plot1.setDomainPannable(true);

		plot1.setDomainCrosshairVisible(true);
		plot1.setDomainCrosshairLockedOnData(true);

		plot1.setRangeCrosshairVisible(true);
		//plot1.setRangeCrosshairValue(4000);
		//plot1.setRangeC

		plot1.setNoDataMessage("No data ...");

		/*
		XYDataset dataset2 = MovingAverage.createMovingAverage(collection, "-MAVG", 3 * 24 * 60 * 60 * 1000L, 0L);
		plot1.setDataset(1, dataset2);
		plot1.setRenderer(1, new StandardXYItemRenderer());
		*/

	//------------------------------------------------------------

		//TimeSeriesCollection dataset = new TimeSeriesCollection();

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
		target.setPaint(ChartColor.LIGHT_GRAY);
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
		
		
	//----------------------------------------------------------------------------------------------------------------------
		
		XYPlot plot4 = new XYPlot(
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
		combinedPlot.add(plot4, 1);
		
		JFreeChart chart = new JFreeChart(combinedPlot);
		chart.setPadding(new RectangleInsets(-7, -15, 0, 0));

		ChartPanel panel = new ChartPanel(chart);
		panel.setDoubleBuffered(true);

		//panel.addOverlay(chOverlay);

		panel.setMouseWheelEnabled(true);
		panel.setMouseZoomable(true);

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
				System.out.println("MS_RELEASED");
				
				Rectangle2D dataArea = panel.getScreenDataArea(e.getX(), e.getY());
				
				System.out.println(						combinedPlot.getDomainAxisEdge()	);

				System.out.println("DATA_AREA: "+dataArea);
				
				if(dataArea != null) {
					double x = dateAxis.java2DToValue(
											dataArea.getX(),
											dataArea,
											combinedPlot.getDomainAxisEdge()
										);
					//long firstTm = ((OHLCItem)collection.getSeries(0).getDataItem(0)).getPeriod().getFirstMillisecond();
				}


				//dataFetchTimes.add(firstTm);

				/*
				if(dataFetchTimes.size() > 0) {
					System.out.println("AAAAA");
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							Request request = new Request.Builder().url("https://api.binance.com/api/v1/klines?symbol=ETHBTC&interval=1m&limit=1000&endTime="+(dataFetchTimes.poll()-1))
																	.build();
							try {
								Response response = client.newCall(request).execute();
								if(!response.isSuccessful()) return;
								
								Moshi moshi = new Moshi.Builder().build();
								JsonAdapter<List<List>> bnJsonAdapter = moshi.adapter(Types.newParameterizedType(List.class, List.class));
								List<List> res = bnJsonAdapter.fromJson(response.body().string());

								((OHLCSeries) collection.getSeries(0)).setNotify(false);
								
								DateTime dt;
								List subRes;
								for(int j=res.size()-1; j>=0; j--) {
									subRes = res.get(j);

									dt = new DateTime(new BigDecimal(subRes.get(0).toString()).longValue());
									//System.out.println( "TS: "+	dt.getMillis() +"; "+dt.getYear()+"-"+dt.getMonthOfYear()+"-"+dt.getDayOfMonth()+" "+dt.getHourOfDay()+":"+dt.getMinuteOfHour()  + "; OP: " + subRes.get(1).toString()	);

									try {
									collection.getSeries(0).add(new OHLCItem(
										new FixedMillisecond(dt.getMillis()),
										Double.parseDouble(subRes.get(1).toString()),	//open,
										Double.parseDouble(subRes.get(2).toString()),	//high,
										Double.parseDouble(subRes.get(3).toString()),	//low,
										Double.parseDouble(subRes.get(4).toString()),	//close
										Double.parseDouble(subRes.get(5).toString())	//volume
									));
									}
									catch (Exception e) {
										// TODO: handle exception
									}
								}
								
								((OHLCSeries) collection.getSeries(0)).setNotify(true);
								
								System.out.println("END OF FETCh");
							}
							catch(Exception e1) {
								e1.printStackTrace();
							}
						}
					}).start();
				}
				*/

				/*
				long firstTm = ((OHLCItem)collection.getSeries(0).getDataItem(0)).getPeriod().getFirstMillisecond();
				DateTime dtFtm = new DateTime(firstTm);
				
				System.out.println("DAX: "+ new BigDecimal(x).longValue() +" : F_TM: "+firstTm+"; T_TM_D: "+dtFtm.getHourOfDay()+":"+dtFtm.getMinuteOfHour());

				if(new BigDecimal(x).longValue() < firstTm) System.out.println("LESS");
				else System.out.println("MORE");

				if(new BigDecimal(x).longValue() > firstTm) return;

				Request request = new Request.Builder().url("https://api.binance.com/api/v1/klines?symbol=ETHBTC&interval=1m&limit=1000&endTime="+firstTm)
														.build();
				try {
					Response response = client.newCall(request).execute();
					if(!response.isSuccessful()) return;
					
					System.out.println(	response.body().string()	);
				}
				catch(Exception e1) {
					e1.printStackTrace();
				}
				*/
				
				/*
				System.out.println("FALL TO SLEEP");
				try {
					Thread.sleep(10000);
				}
				catch(Exception e1) {
					e1.printStackTrace();
				}
				*/
				
				//System.exit(0);

				/*
				DateTime dt = new DateTime(new BigDecimal(x).longValue());
				System.out.println("DT: "+dt.getHourOfDay()+":"+dt.getMinuteOfHour());
				*/

				/*
				dateAxis.valueToJava2D(
					//value,
					//area,
					//edge
				);
				*/
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
				//System.out.println("MOSE MOVED");

				//panel.getScreenDataArea(x, y);

				Rectangle2D dataArea = panel.getScreenDataArea(e.getX(), e.getY());

				if(dataArea != null) {
					//System.out.println("X: "+e.getX());

					double x = dateAxis.java2DToValue(e.getX(), dataArea, combinedPlot.getDomainAxisEdge());
					//double y = priceAxis.java2DToValue(e.getY(), dataArea, plot1.getRangeAxisEdge());

					//System.out.println("X: "+x);

					//plot1.setRangeCrosshairValue(y);
					plot1.setDomainCrosshairValue(x);

					plot2.setDomainCrosshairVisible(true);
					plot2.setDomainCrosshairValue(x);

					plot3.setDomainCrosshairVisible(true);
					plot3.setDomainCrosshairValue(x);
					
					plot4.setDomainCrosshairVisible(true);
					plot4.setDomainCrosshairValue(x);
					
					//combinedPlot.setDomainCrosshairValue(x);
				}
				//else System.out.println("NO DATA AREA");
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

		fr.setBounds(10, 10, 1000, 600);

		fr.add(panel);

		/*
		JButton button = new JButton("Click");
		button.setBounds(10, 10, 100, 30);
		fr.add(button);
		*/

		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);

		reconnect();
	}

	public static void reconnect() {
		if(socket != null) socket.cancel();
		socket = null;

		client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS)
											.retryOnConnectionFailure(true)
											.pingInterval(20, TimeUnit.SECONDS)
											.build();
		socket = client.newWebSocket(
			new Request.Builder()//.url("wss://api.bitfinex.com/ws/2")
									.url("wss://stream.binance.com:9443/stream?streams=ethbtc@kline_1m")
									.build(),
			//new BFWebSocketListener(collection)
			new BNWebSocketListener(collection)
		);
	}
}

class BNWebSocketListener extends WebSocketListener {
	private OHLCSeriesCollection collection;
	private Moshi moshi = new Moshi.Builder().build();
	private JsonAdapter<BNCandleResult> jsonAdapter = moshi.adapter(BNCandleResult.class);

	private OkHttpClient client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS)
															.retryOnConnectionFailure(true)
															.pingInterval(20, TimeUnit.SECONDS)
															.build();
	

	public BNWebSocketListener(OHLCSeriesCollection collection) {
		this.collection = collection;

		Response response;

		try {
			System.out.println("START INITIAL FETCH OF CANDLES ....");
			
			response = client.newCall(new Request.Builder().url("https://api.binance.com/api/v1/klines?symbol=ETHBTC&interval=1m&limit=1000").build()).execute();
			if(!response.isSuccessful()) {
				System.out.println("COULD NOT GET PAIR HISTORY CANDLES");
				System.exit(1);
			}

			JsonAdapter<List<List>> bnJsonAdapter = moshi.adapter(Types.newParameterizedType(List.class, List.class));
			List<List> res = bnJsonAdapter.fromJson(response.body().string());

			((OHLCSeries)collection.getSeries(0)).setNotify(false);

			DateTime dt;
			for(int j=0; j<res.size(); j++) {
				List subRes = res.get(j);

				dt = new DateTime(new BigDecimal(subRes.get(0).toString()).longValue());
				//System.out.println( "TS: "+	dt.getMillis() +"; "+dt.getYear()+"-"+dt.getMonthOfYear()+"-"+dt.getDayOfMonth()+" "+dt.getHourOfDay()+":"+dt.getMinuteOfHour()  + "; OP: " + subRes.get(1).toString()	);

				collection.getSeries(0).add(new OHLCItem(
					new FixedMillisecond(dt.getMillis()),
					Double.parseDouble(subRes.get(1).toString()),	//open,
					Double.parseDouble(subRes.get(2).toString()),	//high,
					Double.parseDouble(subRes.get(3).toString()),	//low,
					Double.parseDouble(subRes.get(4).toString()),	//close
					Double.parseDouble(subRes.get(5).toString())	//volume
				));
			}
			
			((OHLCSeries)collection.getSeries(0)).setNotify(true);
			
			System.out.println("DONE");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFailure(WebSocket socket, Throwable t, Response response) {
		t.printStackTrace();
		
		Test3.reconnect();
	}

	@Override
	public void onClosed(WebSocket webSocket, int code, String reason) {
		Test3.reconnect();
	}

	@Override
	public void onOpen(WebSocket socket, Response response) {
		System.out.println("BN_ON_OPEN");
	}

	@Override
	public void onMessage(WebSocket socket, String text) {
		System.out.println("BN_ON_MESSAGE. "+text);

		if(!text.contains("kline") || collection.getSeriesCount() == 0) return;
		
		BNCandleResult candleResult;
		try {
			candleResult = jsonAdapter.fromJson(text);

			long lastHistoryCandleTS = ((OHLCItem) collection.getSeries(0).getDataItem(collection.getSeries(0).getItemCount()-1)).getPeriod().getFirstMillisecond();

			if(lastHistoryCandleTS == candleResult.data.k.t) {
				System.out.println("UPD_CANDLE_PRICE");

				System.out.println(	"OLD_CL_VAL: "+	String.format("%f", new BigDecimal(((OHLCItem) collection.getSeries(0).getDataItem(collection.getSeries(0).getItemCount()-1)).getCloseValue()).doubleValue())	);

				collection.getSeries(0).updatePrice(new BigDecimal(candleResult.data.k.c).doubleValue());

				System.out.println(	"NEW_CL_VAL: "+ String.format("%f", new BigDecimal(((OHLCItem) collection.getSeries(0).getDataItem(collection.getSeries(0).getItemCount()-1)).getCloseValue()).doubleValue())	);
			}
			else if(candleResult.data.k.t  > lastHistoryCandleTS) {
				System.out.println("ADD_NEW_CANDLE");

				collection.getSeries(0).add(new OHLCItem(
					new FixedMillisecond(new BigDecimal(candleResult.data.k.t).longValue()),	//period,
					Double.parseDouble(candleResult.data.k.o),									//open,
					Double.parseDouble(candleResult.data.k.h),									//high,
					Double.parseDouble(candleResult.data.k.l),									//low,
					Double.parseDouble(candleResult.data.k.c),									//close
					Double.parseDouble(candleResult.data.k.l)									//volume
				));
			}
			else {
				System.out.println("");
				System.out.println("NO_TODO");

				//System.exit(0);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		System.out.println("---------------------------------------------------------------------------------");
	}
}
