import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Bullet {
	public double x, y, direction;
	public int ID;
	public Ellipse2D body;
	public Bullet(double x, double y, double direction, int ID) {
		this.x=x; this.y = y; this.direction=direction; this.ID = ID; this.body = new Ellipse2D.Double(x, y, 10, 10);
	}
	public Rectangle2D updateBody() {
		body.setFrame(new Rectangle2D.Double(body.getX()+Math.cos(direction)*1.5, body.getY() + Math.sin(direction)*1.5, 10, 10));
		return body.getFrame();
	}
}
