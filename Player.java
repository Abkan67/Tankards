import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class Player {
	public double vx, vy, x, y, speed = 1, deg; private String name; private Input input; public int ID; private Rectangle2D body, turret;
	private ArrayList<Bullet> bullets = new ArrayList<>();
	Player(int x,int y, String name, Input input, int ID) {
		this.x=x;this.y=y; this.name=name; this.input = input; this.ID = ID; this.body=new Rectangle2D.Double(); this.turret = new Rectangle2D.Double();
	}
	public void draw(Graphics2D g) {
		g.drawString(this.name, (int)this.x, (int)this.y);
		body = new Rectangle((int)this.x, (int)this.y, 50,50);
		 g.draw(body);
		 g.rotate(this.deg);
		g.draw(turret);
		g.rotate(-this.deg);
		for (Bullet bullet : bullets) g.draw(bullet.body);
	}
	public void update(Graphics2D g){
		double offset = (input.cursorPos.getX()>=x) ? 0 : Math.PI;
		deg = offset + Math.atan((input.cursorPos.getY()-y)/(input.cursorPos.getX()-x));
		turret.setFrameFromDiagonal(
				findAngle(x+25 + 5 * Math.sin(deg),
						y+25 - 5 * Math.cos(deg),
						-deg), 
				findAngle(x+25 + 40 * Math.cos(deg) - 5 * Math.sin(deg),
						y+25 + 40 * Math.sin(deg) + 5 * Math.cos(deg),
						-deg));
		Game.gameClient.sendData(Protocol.MOUSEMOVE03PACKET.createPacket(ID, this.deg));
		this.updateBullets();
		this.calcMove();
		this.draw(g);
	}
	public Point2D findAngle(double x, double y, double angle) {
		double xNew = x * Math.cos(angle) - y *Math.sin(angle);
		double yNew = x * Math.sin(angle) + y * Math.cos(angle);
		return new Point2D.Double(xNew, yNew);
	}
	public void shoot(double angle) {
		Point2D bulletMiddle = new Point2D.Double(x+25 + Math.cos(deg) * 45, y+25 + Math.sin(deg) * 45);
		Bullet bullet = new Bullet(bulletMiddle.getX(), bulletMiddle.getY(), deg, ID);
		bullets.add(bullet);
	}
	public void updateBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			Rectangle2D newHitbox = bullets.get(i).updateBody();
			if (detectCollision(newHitbox)) {
				bullets.remove(bullets.get(i--));
			}
		}
	}
	
	public boolean detectCollision(Rectangle2D hitbox) { // Returns true if collides I don't think this logically fits with what I've done in other boolean methods but it's fine I guess as long as it works and no one has to see this
		if (hitbox.getMaxX() > Game.currentGame.display.getWidth() ||
				hitbox.getMinX() < 0 ||
				hitbox.getMinY() < 0 ||
				hitbox.getMaxY() > Game.currentGame.display.getHeight()) return true;
		for (Rectangle2D barrier: Game.currentGame.barriers) {
			if (hitbox.intersects(barrier)) return true;
		}
		return false;
	}
	
	public String getName(){return this.name;}
	public int getX(){return (int)this.x;}
	public int getY(){return (int)this.y;}
	public int getID() {return this.ID;}
	public void setID(int ID){this.ID = ID;}
	public void setAngle(double angle) {this.deg=angle;}

	private void calcMove(){
		if (this.input == null) return;
		this.vy = 0; this.vx = 0;
		if(this.input.upArrow.isPressed() && checkCollision(0, -speed)) {this.vy = -speed;}
		if(this.input.downArrow.isPressed() && checkCollision(0, speed)) {this.vy = speed;}
		if(this.input.rightArrow.isPressed() && checkCollision(speed, 0)) {this.vx = speed;}
		if(this.input.leftArrow.isPressed() && checkCollision(-speed, 0)) {this.vx = -speed;}
		this.move();
	}
	
	public boolean checkCollision(double dx, double dy) {
		Rectangle2D temp = new Rectangle2D.Double(body.getX()+dx, body.getY()+dy, 50, 50);
		for (Rectangle2D barrier : Game.currentGame.barriers) {
			if (temp.intersects(barrier)) return false;
		}
		return true;
	}
	
	private void move() {
		this.x+=vx;
		this.y+=vy;
		if(vy==0 && vx==0){return;}
		Game.gameClient.sendData(Protocol.MOVE02PACKET.createPacket(ID, (int)this.x, (int)this.y));
	}
	public void move(int x, int y) {
		this.x+=x; this.y+=y;
	}
}
