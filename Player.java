import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Player {
	public static int playerDims = 50;
	public double vx, vy, x, y, speed = 1, deg; private String name; private Input input; public int ID; private Rectangle2D body, turret;
	private ArrayList<Bullet> bullets = new ArrayList<>();
	public double mouseX, mouseY;
	public boolean isAlive = true;
	Player(int x,int y, String name, Input input, int ID) {
		this.x=x;this.y=y; this.name=name; this.input = input; this.ID = ID; this.body=new Rectangle2D.Double(); this.turret = new Rectangle2D.Double();
	}

    public void setName(String name) {
        this.name = name;
    }
	
	public void draw(Graphics2D g) {
		g.drawString(""+this.name, (int)this.x, (int)this.y);
		body = new Rectangle((int)this.x, (int)this.y, Player.playerDims,Player.playerDims);
		 g.draw(body);
		 g.rotate(this.deg);
		g.draw(turret);
		g.rotate(-this.deg);
		for (Bullet bullet : bullets) g.draw(bullet.body);
	}
	public void drawBlindness(Graphics2D g) {
		
	}
	public void update(Graphics2D g){
		if(!this.isAlive) return;
		double offset = (this.mouseX>=x+25) ? 0 : Math.PI;
		deg = offset + Math.atan((this.mouseY-(y+25))/(this.mouseX-(x+25)));
		turret.setFrameFromDiagonal(
				findAngle(x+25 + 5 * Math.sin(deg),
						y+25 - 5 * Math.cos(deg),
						-deg), 
				findAngle(x+25 + 40 * Math.cos(deg) - 5 * Math.sin(deg),
						y+25 + 40 * Math.sin(deg) + 5 * Math.cos(deg),
						-deg));
		this.cooldown--;
		this.updateBullets();
		this.calcMove();
		this.draw(g);
	}
	public Point2D findAngle(double x, double y, double angle) {
		double xNew = x * Math.cos(angle) - y *Math.sin(angle);
		double yNew = x * Math.sin(angle) + y * Math.cos(angle);
		return new Point2D.Double(xNew, yNew);
	}
	private int cooldown = 0;
	public boolean shoot(double angle) {
		if(cooldown>0) return false;
		Point2D bulletMiddle = new Point2D.Double(x+25 + Math.cos(deg) * 45, y+25 + Math.sin(deg) * 45);
		Bullet bullet = new Bullet(bulletMiddle.getX(), bulletMiddle.getY(), deg, ID);
		bullets.add(bullet);
		cooldown = 100;
		return true;
	}



	public void updateBullets() { //updates all bullets or???
		for (int i = 0; i < bullets.size(); i++) {
			Rectangle2D newHitbox = bullets.get(i).updateBody();
			if (detectCollision(newHitbox)) {
				bullets.remove(bullets.get(i--));
			}
			for(PlayerConnection player : Game.currentGame.client.getAllPlayers()){
				if (player.inHitBox(newHitbox)){
					player.die();
					bullets.remove(bullets.get(i--));
				}
			}
		}
	}
	public boolean inHitBox(Rectangle2D object){
		return new Rectangle((int)this.x,(int)this.y,
		Player.playerDims,Player.playerDims).intersects(object);
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
	public void die() {
		isAlive = false;
		this.x = 1000;
		this.y=1000;
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
		Rectangle2D temp = new Rectangle2D.Double(body.getX()+dx, body.getY()+dy, Player.playerDims, Player.playerDims);
		for (Rectangle2D barrier : Game.currentGame.barriers) {
			if (temp.intersects(barrier)) {return false;}
		}
		return !(temp.getMinX()<0 || temp.getMinY()<0 ||
		temp.getMaxX()>Game.currentGame.display.getWidth()
		|| temp.getMaxY()>Game.currentGame.display.getHeight());
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
