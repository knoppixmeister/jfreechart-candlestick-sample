import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.panel.AbstractOverlay;
import org.jfree.chart.panel.Overlay;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.statistics.HistogramDataset;

public class CustomOverlay extends AbstractOverlay implements Overlay {
	@Override
	public void paintOverlay(Graphics2D g2, ChartPanel chartPanel) {
		System.out.println("REDRAW CUSTOM_OVERLAY");

		NumberAxis xAxis = new NumberAxis("");
    	xAxis.setAutoRangeIncludesZero(false);
    	
    	ValueAxis yAxis = new NumberAxis("");

    	XYBarRenderer renderer = new XYBarRenderer();
    	//if(tooltips) renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());

        //if(urls) renderer.setURLGenerator(new StandardXYURLGenerator());

    	HistogramDataset dataset = new HistogramDataset();

    	double[] values = new double[1000];
    	Random generator = new Random(12345678L);

    	for(int i = 0; i < 1000; i++) {
    		values[i] = generator.nextGaussian() + 5;
    	}
    	dataset.addSeries("H1", values, 100, 2.0, 8.0);
    	
        XYPlot plot = new XYPlot(
        	dataset,
        	xAxis,
        	yAxis,
        	renderer
        );
        //plot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);
        
        //plot.setBackgroundAlpha(0f);
        plot.setBackgroundPaint(null);
        
        plot.getRangeAxis().setInverted(true);
        
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
       
        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setBackgroundPaint(null);
		
        chart.draw(g2, new Rectangle(chartPanel.getX(), chartPanel.getY(), chartPanel.getWidth(), chartPanel.getHeight()-100 ));
	}
}
