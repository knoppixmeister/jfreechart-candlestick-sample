package org.jfree.chart.ui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * An interface for an object that can draw itself within an area on a 
 * {@code Graphics2D}.
 */
public interface Drawable {
    public void draw(Graphics2D g2, Rectangle2D area);
}
