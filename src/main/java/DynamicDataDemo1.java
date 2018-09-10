import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class DynamicDataDemo1 extends ApplicationFrame {
	private static final long serialVersionUID = 1529244212694434231L;

	/**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public DynamicDataDemo1(String title) {
    	super(title);
    	
    	MyDemoPanel demoPanel = new MyDemoPanel();
    	setContentPane(demoPanel);
    }

	static class MyDemoPanel extends DemoPanel implements ActionListener {
		private static final long serialVersionUID = 8374904452290244137L;

		/** The time series data. */
        private TimeSeries series;

        /** The most recent value added. */
        private double lastValue = 100.0;

        public MyDemoPanel() {
        	super(new BorderLayout());

            series = new TimeSeries("Random Data");
            TimeSeriesCollection dataset = new TimeSeriesCollection(series);
            
            ChartPanel chartPanel = new ChartPanel(createChart(dataset));
            chartPanel.setPreferredSize(new java.awt.Dimension(1000, 470));
            
            //chartPanel.setMouseWheelEnabled(true);
            
            addChart(chartPanel.getChart());
 
            /*
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            
            JButton button = new JButton("Add New Data Item");
            button.setActionCommand("ADD_DATA");
            button.addActionListener(this);
            buttonPanel.add(button);
            */

            add(chartPanel);
            //add(buttonPanel, BorderLayout.SOUTH);
        }

        /**
         * Creates a sample chart.
         *
         * @param dataset  the dataset.
         *
         * @return A sample chart.
         */
        private JFreeChart createChart(XYDataset dataset) {
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "",
                "",
                "Value",
                dataset,
                true,
                true,
                false
            );
            chart.removeLegend();

            XYPlot plot = 	chart.getXYPlot();
            				//(XYPlot)chart.getPlot();
            ValueAxis axis = plot.getDomainAxis();
            //axis.setAutoRange(true);
            axis.setFixedAutoRange(6000.0);  // 60 seconds
            //axis = plot.getRangeAxis();
            //axis.setRange(0.0, 200.0);
            
            new Thread(new Runnable() {
				@Override
				public void run() {
					while(true) {
						double factor = 0.90 + 0.2 * Math.random();
		                lastValue = lastValue * factor;
						series.add(new Millisecond(), lastValue);
						
						
						try {
							Thread.sleep(10);
						}
						catch (Exception e) {
						}
						
					}
				}
			}).start();

            return chart;
        }

        /**
         * Handles a click on the button by adding new (random) data.
         *
         * @param e  the action event.
         */

        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("ADD_DATA")) {
                double factor = 0.90 + 0.2 * Math.random();
                lastValue = lastValue * factor;
                Millisecond now = new Millisecond();
                
                System.out.println("Now = " + now.toString());
                
                series.add(new Millisecond(), lastValue);
            }
        }
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        return new DynamicDataDemo1.MyDemoPanel();
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        DynamicDataDemo1 demo = new DynamicDataDemo1("JFreeChart: DynamicDataDemo1.java");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}
