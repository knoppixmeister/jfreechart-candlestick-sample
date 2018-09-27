package org.jfree.chart;

/**
 * A source of legend items.  A {@link org.jfree.chart.title.LegendTitle} will
 * maintain a list of sources (often just one) from which it obtains legend
 * items.
 */
public interface LegendItemSource {
    /**
     * Returns a (possibly empty) collection of legend items.
     *
     * @return The legend item collection (never {@code null}).
     */
    public LegendItemCollection getLegendItems();
}
