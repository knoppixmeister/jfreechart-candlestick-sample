package org.jfree.chart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 * A frame for displaying a chart.
 */
public class ChartFrame extends JFrame {
	private static final long serialVersionUID = -3996832757395355935L;
	
	/** The chart panel. */
    private ChartPanel chartPanel;

    /**
     * Constructs a frame for a chart.
     *
     * @param title  the frame title.
     * @param chart  the chart.
     */
    public ChartFrame(String title, JFreeChart chart) {
        this(title, chart, false);
    }

    /**
     * Constructs a frame for a chart.
     *
     * @param title  the frame title.
     * @param chart  the chart.
     * @param scrollPane  if {@code true}, put the Chart(Panel) into a
     *                    JScrollPane.
     */
    public ChartFrame(String title, JFreeChart chart, boolean scrollPane) {
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.chartPanel = new ChartPanel(chart, false);
        if(scrollPane) setContentPane(new JScrollPane(chartPanel));
        else setContentPane(chartPanel);
	}

    /**
     * Returns the chart panel for the frame.
     *
     * @return The chart panel.
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
