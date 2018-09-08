import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

public class Test2 {
	public static void main(String[] args) {
		JFrame fr = new JFrame();

		OHLCSeriesCollection collection = new OHLCSeriesCollection();

		OHLCSeries series = new OHLCSeries("");
		series.addChangeListener(new SeriesChangeListener() {
			@Override
			public void seriesChanged(SeriesChangeEvent event) {
				double close = ((OHLCItem)series.getDataItem(0)).getCloseValue();

				System.out.println("SERIES_CHANGED: CL: "+close);
			}
		});

		collection.addSeries(series);

		collection.addChangeListener(new DatasetChangeListener() {
			@Override
			public void datasetChanged(DatasetChangeEvent event) {
				OHLCItem item = (OHLCItem)collection.getSeries(0).getDataItem(0);

				if(item != null) {
					double val = ((OHLCItem)collection.getSeries(0).getDataItem(0)).getCloseValue();

					System.out.println("COLLECTION_CH_EVENT. CL_VAL: "+val);
				}
			}
		});

		long time = System.currentTimeMillis();

		series.add(
			new OHLCItem(
				new FixedMillisecond(time),
				5000,	//open,
				8000,	//high,
				4000,	//low,
				7000	//close
			)
		);
		series.add(
			new OHLCItem(
				new FixedMillisecond(time+1000),
				4000,	//open,
				9000,	//high,
				6000,	//low,
				8000	//close
			)
		);
	
		DateAxis dateAxis = new DateAxis();
		NumberAxis priceAxis = new NumberAxis();

		XYPlot plot = new XYPlot(
			collection,//dataset,
			dateAxis,//domainAxis,
			priceAxis,//rangeAxis,
			new CandlestickRenderer(3)	//renderer
		);

		JFreeChart chart = new JFreeChart(plot);

		chart.removeLegend();
		
		ChartPanel panel = new ChartPanel(chart);
		
		fr.setBounds(100, 10, 800, 500);

		fr.add(panel);

		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setVisible(true);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Random r = new Random();

				while(true) {
					double val = r.nextInt(12000-7500)+7500;

					collection.getSeries(0).updatePrice(val);

					//((OHLCItem)series.getDataItem(0)).updatePrice(9000);

					System.out.println("UP_VAl: "+val);
					
					try {
						Thread.sleep(1000);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					
					System.out.println("");
				}
			}
		}).start();
	}
}
