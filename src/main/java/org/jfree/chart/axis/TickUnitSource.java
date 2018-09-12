package org.jfree.chart.axis;

/**
 * An interface used by the {@link DateAxis} and {@link NumberAxis} classes to
 * obtain a suitable {@link TickUnit}.
 */
public interface TickUnitSource {
    /**
     * Returns the smallest tick unit available in the source that is larger
     * than {@code unit} or, if there is no larger unit, returns {@code unit}.
     *
     * @param unit  the unit ({@code null} not permitted).
     *
     * @return A tick unit that is larger than the supplied unit.
     */
    public TickUnit getLargerTickUnit(TickUnit unit);

    /**
     * Returns the tick unit in the collection that is greater than or equal
     * to (in size) the specified unit.
     *
     * @param unit  the unit.
     *
     * @return A unit from the collection.
     */
    public TickUnit getCeilingTickUnit(TickUnit unit);

    /**
     * Returns the smallest tick unit available in the source that is greater 
     * than or equal to the specified size.  If there is no such tick unit,
     * the method should return the largest available tick in the source.
     *
     * @param size  the size.
     *
     * @return A unit from the collection (never {@code null}).
     */
    public TickUnit getCeilingTickUnit(double size);
}
