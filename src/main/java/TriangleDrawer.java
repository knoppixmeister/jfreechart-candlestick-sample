import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.ui.Drawable;

public class TriangleDrawer implements Drawable {
	private Paint 	outlinePaint;
    private Stroke 	outlineStroke;
    private Paint 	fillPaint;

    public TriangleDrawer(Paint outlinePaint, Stroke outlineStroke, Paint fillPaint) {
    	this.outlinePaint 	= outlinePaint;
    	this.outlineStroke 	= outlineStroke;
    	this.fillPaint 		= fillPaint;
	}

	@Override
	public void draw(Graphics2D g2, Rectangle2D area) {
		System.out.println("TRD");

		//Ellipse2D ellipse = new Ellipse2D.Double(area.getX(), area.getY(), area.getWidth(), area.getHeight());

		//System.out.println("X: "+area.getX()+"; Y: "+area.getY()+"; W: "+area.getWidth()+"; H: "+area.getHeight());
		//System.out.println("CENT_X: "+area.getCenterX());

		g2.setPaint(Color.RED);
		//g2.fill(ellipse);
		//g2.drawLine((int)area.getX()+5, (int)area.getY(), 15, 10);
		//g2.drawLine((int)area.getX()+5, (int)area.getY(), -5, 10);
		//g2.drawLine(-5, 10, 15, 10);

		Polygon p = new Polygon();
		p.addPoint((int)area.getX()+5, (int)area.getY());
		p.addPoint(15, 10);
		p.addPoint(-5, 10);
		p.addPoint((int)area.getX()+5, (int)area.getY());

		g2.fillPolygon(p);
		
		//Point2D point2 = new Point2D.Double(area.getX()+area.getWidth(), area.getY());
		//Point2D point3 = new Point2D.Double(area.getX()+area.getWidth(), area.getY());
		
		//Point point2 = new Point(area.getX()+area.getWidth(), area.getY());
		//Point point3 = new Point(location.x+(width/2),location.y - height);
	}
}
