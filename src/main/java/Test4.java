import java.awt.Rectangle;
import java.util.concurrent.TimeUnit;

import javax.servlet.jsp.JspFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Test4 {
	public static void main(String[] args) {
		OHLCSeriesCollection collection = new OHLCSeriesCollection();
		OHLCSeries series = new OHLCSeries("");
		collection.addSeries(series);

		DateAxis dateAxis = new DateAxis();
		NumberAxis priceAxis = new NumberAxis();
		priceAxis.setAutoRangeIncludesZero(false);

		CandlestickRenderer renderer = new CandlestickRenderer(CandlestickRenderer.WIDTHMETHOD_AVERAGE);

		XYPlot plot1 = new XYPlot(
			collection,	//dataset,
			null,		//domainAxis,
			priceAxis,	//rangeAxis,
			renderer		//renderer
		);
		plot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		plot1.setRangePannable(true);

		CombinedDomainXYPlot combinedPlot = new CombinedDomainXYPlot(dateAxis);
		//combinedPlot.setRangeAxis(priceAxis);
		
		//combinedPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		
		combinedPlot.setRangePannable(true);
		//combinedPlot.setDomainPannable(true);
		
		combinedPlot.add(plot1);
		
		JFreeChart chart = new JFreeChart(combinedPlot);
		chart.removeLegend();
		
		ChartPanel panel = new ChartPanel(chart);
		
		JFrame fr = new JFrame();
		fr.setBounds(new Rectangle(10, 10, 1000, 500));

		fr.add(panel);
		
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);
		
		OkHttpClient client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.SECONDS)
														.retryOnConnectionFailure(true)
														.build();
		
		client.newWebSocket(
			new Request.Builder().url("wss://api.bitfinex.com/ws/2")
								//.url("wss://stream.binance.com:9443/stream?streams=ethbtc@kline_5m")
								.build(),
			new BFWebSocketListener(collection)
			//new BNWebSocketListener()
		);
	}
}
