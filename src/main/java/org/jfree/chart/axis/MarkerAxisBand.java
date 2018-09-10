package org.jfree.chart.axis;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.ObjectUtils;

/**
 * A band that can be added to a number axis to display regions.
 */
public class MarkerAxisBand implements Serializable {
    private static final long serialVersionUID = -1729482413886398919L;

    /** The axis that the band belongs to. */
    private NumberAxis axis;

    /** The top outer gap. */
    private double topOuterGap;

    /** The top inner gap. */
    private double topInnerGap;

    /** The bottom outer gap. */
    private double bottomOuterGap;

    /** The bottom inner gap. */
    private double bottomInnerGap;

    /** The font. */
    private Font font;

    /** Storage for the markers. */
    private List markers;

    /**
     * Constructs a new axis band.
     *
     * @param axis  the owner.
     * @param topOuterGap  the top outer gap.
     * @param topInnerGap  the top inner gap.
     * @param bottomOuterGap  the bottom outer gap.
     * @param bottomInnerGap  the bottom inner gap.
     * @param font  the font.
     */
    public MarkerAxisBand(
    	NumberAxis axis,
    	double topOuterGap,
    	double topInnerGap,
    	double bottomOuterGap,
    	double bottomInnerGap,
    	Font font)
    {
        this.axis 			=	axis;
        this.topOuterGap 	=	topOuterGap;
        this.topInnerGap 	=	topInnerGap;
        this.bottomOuterGap =	bottomOuterGap;
        this.bottomInnerGap =	bottomInnerGap;
        this.font 			=	font;
        markers				=	new java.util.ArrayList();
    }

    /**
     * Adds a marker to the band.
     *
     * @param marker  the marker.
     */
    public void addMarker(IntervalMarker marker) {
    	markers.add(marker);
    }

    /**
     * Returns the height of the band.
     *
     * @param g2  the graphics device.
     *
     * @return The height of the band.
     */
    public double getHeight(Graphics2D g2) {
        double result = 0.0;
        
        if(markers != null && markers.size() > 0) {
            LineMetrics metrics = font.getLineMetrics("123g", g2.getFontRenderContext());
            result = topOuterGap + topInnerGap + metrics.getHeight() + bottomInnerGap + bottomOuterGap;
        }
        
        return result;
    }

    /**
     * A utility method that draws a string inside a rectangle.
     *
     * @param g2  the graphics device.
     * @param bounds  the rectangle.
     * @param font  the font.
     * @param text  the text.
     */
    private void drawStringInRect(
    	Graphics2D g2,
    	Rectangle2D bounds,
    	Font font,
    	String text)
    {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics(font);
        Rectangle2D r = TextUtils.getTextBounds(text, g2, fm);
        double x = bounds.getX();
        if(r.getWidth() < bounds.getWidth()) x = x + (bounds.getWidth() - r.getWidth()) / 2;

        LineMetrics metrics = font.getLineMetrics(text, g2.getFontRenderContext());
        g2.drawString(text, (float) x, (float) (bounds.getMaxY() - bottomInnerGap - metrics.getDescent()));
    }

    /**
     * Draws the band.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public void draw(
    	Graphics2D g2,
    	Rectangle2D plotArea,
    	Rectangle2D dataArea,
    	double x,
    	double y)
    {
        double h = getHeight(g2);
        Iterator iterator = markers.iterator();
        
        while(iterator.hasNext()) {
            IntervalMarker marker = (IntervalMarker) iterator.next();
            double start 	=	Math.max(marker.getStartValue(), axis.getRange().getLowerBound());
            double end 		=	Math.min(marker.getEndValue(), axis.getRange().getUpperBound());
            double s 		=	axis.valueToJava2D(start, dataArea, RectangleEdge.BOTTOM);
            double e 		=	axis.valueToJava2D(end, dataArea, RectangleEdge.BOTTOM);
            Rectangle2D r = new Rectangle2D.Double(s, y + topOuterGap, e - s, h - topOuterGap - bottomOuterGap);

            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.fill(r);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(r);
            g2.setComposite(originalComposite);

            g2.setPaint(Color.BLACK);
            drawStringInRect(g2, r, font, marker.getLabel());
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Tests this axis for equality with another object.  Note that the axis
     * that the band belongs to is ignored in the test.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MarkerAxisBand)) {
            return false;
        }
        MarkerAxisBand that = (MarkerAxisBand) obj;
        if (this.topOuterGap != that.topOuterGap) {
            return false;
        }
        if (this.topInnerGap != that.topInnerGap) {
            return false;
        }
        if (this.bottomInnerGap != that.bottomInnerGap) {
            return false;
        }
        if (this.bottomOuterGap != that.bottomOuterGap) {
            return false;
        }
        if (!ObjectUtils.equal(this.font, that.font)) {
            return false;
        }
        if (!ObjectUtils.equal(this.markers, that.markers)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for the object.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = 37;
        result = 19 * result + this.font.hashCode();
        result = 19 * result + this.markers.hashCode();
        return result;
    }
}
