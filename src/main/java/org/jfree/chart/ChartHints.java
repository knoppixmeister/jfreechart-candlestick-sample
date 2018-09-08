package org.jfree.chart;

import java.util.Map;

/**
 * Special rendering hints that can be used internally by JFreeChart or by
 * specialised implementations of the {@code Graphics2D} API.  For example,
 * JFreeSVG's {@code SVGGraphics2D} class, will use the 
 * {@code KEY_BEGIN_ELEMENT} and {@code KEY_END_ELEMENT} hints to drive the 
 * output content.
 * 
 * @since 1.0.18
 */
public final class ChartHints {
    private ChartHints() {  
    }

    /**
     * The key for a hint to signal the beginning of an element.  The value
     * should be a string containing the element id or, alternatively, a Map 
     * containing the 'id' (String) and 'ref' (String in JSON format).
     */
    public static final Key KEY_BEGIN_ELEMENT = new ChartHints.Key(0);
    
    /**
     * The key for a hint that ends an element.
     */
    public static final Key KEY_END_ELEMENT = new ChartHints.Key(1);
    
    /**
     * A key for rendering hints that can be used with JFreeChart (in 
     * addition to the regular Java2D rendering hints).
     */
    public static class Key extends java.awt.RenderingHints.Key {

        /**
         * Creates a new key.
         * 
         * @param privateKey  the private key. 
         */
        public Key(int privateKey) {
            super(privateKey);    
        }
    
        /**
         * Returns {@code true} if {@code val} is a value that is
         * compatible with this key, and {@code false} otherwise.
         * 
         * @param val  the value.
         * 
         * @return A boolean. 
         */
        @Override
        public boolean isCompatibleValue(Object val) {
            switch (intKey()) {
                case 0:
                    return val == null || val instanceof String 
                            || val instanceof Map;
                case 1:
                    return val == null || val instanceof Object;
                default:
                    throw new RuntimeException("Not possible!");
            }
        }
    }
    
}
