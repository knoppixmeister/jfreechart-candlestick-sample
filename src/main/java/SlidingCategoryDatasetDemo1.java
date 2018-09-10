import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.ui.ApplicationFrame;

/**
 * A demo for the {@link SlidingCategoryDataset}.  We use a JScrollBar to
 * control the subset of a dataset that is made visible via the
 * {@link SlidingCategoryDataset} - this simulates scrolling by redrawing
 * the chart with different bars visible.  It isn't ideal, because there is
 * no reliable way to align the JScrollBar with the data area on the chart,
 * but it is likely to be good enough for many applications.
 */
public class SlidingCategoryDatasetDemo1 extends ApplicationFrame {
	private static final long serialVersionUID = 9029736000670637248L;

	static class MyDemoPanel extends DemoPanel implements ChangeListener {
		private static final long serialVersionUID = -7008324568617514972L;

		/** A scrollbar to update the dataset value. */
        JScrollBar scroller;

        /** The dataset. */
        SlidingCategoryDataset dataset;

        /**
         * Creates a new demo panel.
         */
        public MyDemoPanel() {
            super(new BorderLayout());
            
            dataset = new SlidingCategoryDataset(createDataset(), 0, 20);

            // get data for diagrams
            JFreeChart chart = createChart(dataset);
            addChart(chart);
            ChartPanel cp1 = new ChartPanel(chart);
            cp1.setPreferredSize(new Dimension(900, 500));
            
            //cp1.setMouseZoomable(true);
            //cp1.setMouseWheelEnabled(true);
    		//cp1.setDoubleBuffered(true);
            
            scroller = new JScrollBar(SwingConstants.VERTICAL, 0, 20, 0, 50);
            add(cp1);
            scroller.getModel().addChangeListener(this);
            JPanel scrollPanel = new JPanel(new BorderLayout());
            scrollPanel.add(this.scroller);
            scrollPanel.setBorder(BorderFactory.createEmptyBorder(66, 2, 2, 2));
            scrollPanel.setBackground(Color.WHITE);
            add(scrollPanel, BorderLayout.EAST);
        }

        /**
         * Returns a sample dataset.
         *
         * @return The dataset.
         */
        private static CategoryDataset createDataset() {
        	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        	for(int i=0; i<50; i++) {
        		dataset.addValue(Math.random() * 100.0, "S1", "Series " + i);
        	}

            return dataset;
        }

        /**
         * Creates a sample chart.
         *
         * @param dataset  the dataset.
         *
         * @return The chart.
         */
        private static JFreeChart createChart(CategoryDataset dataset) {
            // create the chart...
            JFreeChart chart = ChartFactory.createBarChart(
                "SlidingCategoryDatasetDemo1",         // chart title
                "Series",               // domain axis label
                "Value",                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                false,                     // include legend
                true,                     // tooltips?
                false                     // URLs?
            );

            CategoryPlot plot = (CategoryPlot) chart.getPlot();

            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setMaximumCategoryLabelWidthRatio(0.8f);
            domainAxis.setLowerMargin(0.02);
            domainAxis.setUpperMargin(0.02);

            // set the range axis to display integers only...
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setRange(0.0, 100.0);

            // disable bar outlines...
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);

            // set up gradient paints for series...
            GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));
            renderer.setSeriesPaint(0, gp0);

            return chart;
        }

        /**
         * Handle a change in the slider by updating the dataset value.  This
         * automatically triggers a chart repaint.
         *
         * @param e  the event.
         */
        public void stateChanged(ChangeEvent e) {
        	System.out.println(scroller.getValue());

        	dataset.setFirstCategoryIndex(scroller.getValue());
        }
    }

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public SlidingCategoryDatasetDemo1(String title) {
        super(title);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createDemoPanel());
    }

    /**
     * Creates a demo panel.  This method is called by SuperDemo.java.
     *
     * @return A demo panel.
     */
    public static JPanel createDemoPanel() {
        return new MyDemoPanel();
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        SlidingCategoryDatasetDemo1 demo = new SlidingCategoryDatasetDemo1("JFreeChart: SlidingCategoryDatasetDemo1.java");
        demo.pack();
        //RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}