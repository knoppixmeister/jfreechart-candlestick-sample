package org.jfree.chart.util;

/**
 * A utility class for checking method arguments.
*/
public class Args {
    /**
     * Throws an {@code IllegalArgumentException} if the supplied 
     * {@code param} is {@code null}.
     *
     * @param param  the parameter to check ({@code null} permitted).
     * @param name  the name of the parameter (to use in the exception message
     *     if {@code param} is {@code null}).
     *
     * @throws IllegalArgumentException  if {@code param} is 
     *     {@code null}.
     */
    public static void nullNotPermitted(Object param, String name) {
    	if(param == null) throw new IllegalArgumentException("NULL '" + name + "' argument.");
    }

    /**
     * Throws an {@code IllegalArgumentException} if {@code value} is negative.
     * 
     * @param value  the value.
     * @param name  the parameter name (for use in the exception message).
     */
    public static void requireNonNegative(int value, String name) {
        if(value < 0) {
            throw new IllegalArgumentException("Require '" + name + "' (" + value + ") to be non-negative.");
        }
    }

    /**
     * Checks that the value falls within the specified range and, if it does
     * not, throws an {@code IllegalArgumentException}.
     * 
     * @param value  the value.
     * @param name  the parameter name.
     * @param lowerBound  the lower bound of the permitted range.
     * @param upperBound  the upper bound fo the permitted range.
     */
    public static void requireInRange(int value, String name, int lowerBound, int upperBound) {
    	if(value < lowerBound) {
            throw new IllegalArgumentException("Require '" + name + "' (" + value + ") to be in the range " + lowerBound + " to " + upperBound);
    	}
    }
}
