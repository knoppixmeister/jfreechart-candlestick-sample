import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import com.orsoncharts.marker.MarkerDataType;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class Test5 extends JFrame {
	private static final long serialVersionUID = -674380547663491189L;

	public static XYSeries priceSeries = new XYSeries("");

	public static OkHttpClient httpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true)
																		.pingInterval(10, TimeUnit.SECONDS)
																		.build();
	public static Moshi moshi = new Moshi.Builder().build();

	public Test5() {
		XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(priceSeries);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        NumberAxis priceAxis = new NumberAxis();
        priceAxis.setAutoRangeIncludesZero(false);
        DateAxis dateAxis = new DateAxis();

        XYPlot pricePlot = new XYPlot(
			dataset,
			dateAxis,
			priceAxis,
			renderer
		);
		pricePlot.setDomainPannable(true);

		ValueMarker marker = new ValueMarker(0.031849);
		marker.setPaint(Color.BLUE);
		marker.setStroke(new BasicStroke(2f));
		pricePlot.addRangeMarker(marker);

	//-----------------------------------------------------------------------------------------------------------

		XYBarRenderer renderer2 = new XYBarRenderer() {
			public Paint getItemPaint(int series, int item) {
				XYDataset dataset = getPlot().getDataset();
				if(dataset.getYValue(series, item) >= 0.0) return Color.red;
				else return Color.green;
			}
		};
		renderer2.setDrawBarOutline(false);

		XYSeries series2 = new XYSeries("Series 1");
		series2.add(1.0, 5.0);
		series2.add(2.0, 70.8);
		series2.add(300, 48.3);

		XYSeriesCollection collection = new XYSeriesCollection();
		collection.addSeries(series2);

		XYPlot depthPlot = new XYPlot(
			new XYBarDataset(collection, 0.9),	//dataset,
			new NumberAxis(),					//domainAxis,
			null,								//rangeAxis,
			renderer2							//renderer
		);
		depthPlot.setOrientation(PlotOrientation.HORIZONTAL);
		depthPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		//depthPlot.getRangeAxis().setInverted(true);

		CombinedRangeXYPlot combinedRangeXYPlot = new CombinedRangeXYPlot(priceAxis);

		combinedRangeXYPlot.add(pricePlot);
		//combinedRangeXYPlot.add(depthPlot);

		JFreeChart chart = new JFreeChart(combinedRangeXYPlot);
		chart.removeLegend();

		//JFreeChart depthChart = new JFreeChart(depthPlot);

		ChartPanel panel = new ChartPanel(chart);
		add(panel);

		//ChartPanel priceChartPanel = new ChartPanel(chart);
		//ChartPanel depthChartPanel = new ChartPanel(depthChart);

		//add(priceChartPanel, BorderLayout.WEST);
		//add(depthChartPanel);

		setBounds(10, 10, 1000, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);

		httpClient.newWebSocket(
			new Request.Builder().url("wss://stream.binance.com:9443/stream?streams=ethbtc@kline_1m").build(),
			new BNWSListener()
		);
	}

	public static void main(String[] args) {
		new Test5();
	}
}

class BNWSListener extends WebSocketListener {
	private JsonAdapter<BNKlineResult> klineJsonAdapter;
	private BNKlineResult klineResult;

	public BNWSListener() {
		klineJsonAdapter = Test5.moshi.adapter(BNKlineResult.class);
	}

	@Override
	public void onOpen(WebSocket webSocket, Response response) {
		System.out.println("BN_SOCK_ON_OPEN");
	}

	@Override
	public void onMessage(WebSocket webSocket, String text) {
		System.out.println("BN_ON_MESSAGE: "+text+"\r\n");

		klineResult = null;
		try {
			klineResult = klineJsonAdapter.fromJson(text);
		}
		catch(Exception e) {
			e.printStackTrace();

			return;
		}
		if(klineResult == null) return;

		Test5.priceSeries.add(
			new BigDecimal(klineResult.data.E).longValue(),
			new BigDecimal(klineResult.data.k.c).doubleValue()
		);

		System.out.println("-----------------------------------------------------------------------------------------");
	}
}
