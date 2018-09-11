package org.jfree.chart.editor;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.util.Args;

/**
 * The central point for obtaining {@link ChartEditor} instances for editing
 * charts.  Right now, the API is minimal - the plan is to extend this class
 * to provide customisation options for chart editors (for example, make some
 * editor items read-only).
 */
public class ChartEditorManager {
    /** This factory creates new {@link ChartEditor} instances as required. */
    static ChartEditorFactory factory = new DefaultChartEditorFactory();

    /**
     * Private constructor prevents instantiation.
     */
    private ChartEditorManager() {
    }

    /**
     * Returns the current factory.
     *
     * @return The current factory (never {@code null}).
     */
    public static ChartEditorFactory getChartEditorFactory() {
        return factory;
    }

    /**
     * Sets the chart editor factory.
     *
     * @param f  the new factory ({@code null} not permitted).
     */
    public static void setChartEditorFactory(ChartEditorFactory f) {
        Args.nullNotPermitted(f, "f");
        
        factory = f;
    }

    /**
     * Returns a component that can be used to edit the given chart.
     *
     * @param chart  the chart.
     *
     * @return The chart editor.
     */
    public static ChartEditor getChartEditor(JFreeChart chart) {
        return factory.createEditor(chart);
    }
}
