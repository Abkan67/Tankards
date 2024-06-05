import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class Player {
	private double vx; private double vy;
	private double x; private double y; private String name; private Input input; private double speed = 1; private int ID;
	Player(int x,int y, String name, Input input, int ID) {
		this.x=x;this.y=y; this.name=name; this.input = input; this.ID = ID;
	}
	public void draw(Graphics2D g) {
		g.drawString(this.name, (int)this.x, (int)this.y);
		g.setColor(Color.GREEN);
		g.fillRect((int)this.x, (int)this.y, 10,10);
		g.setColor(Color.BLUE);
		g.fill(new Ellipse2D.Double((int)this.x, (int)this.y, 10,10));
	}
	public void update(Graphics2D g){
		this.calcMove();
		this.draw(g);
	}
	public String getName(){return this.name;}
	public int getX(){return (int)this.x;}
	public int getY(){return (int)this.y;}
	public int getID() {return this.ID;}
	public void setID(int ID){this.ID = ID;}

	private void calcMove(){
		if (this.input == null) return;
		this.vy = 0; this.vx = 0;
		if(this.input.upArrow.isPressed()) {this.vy = -speed;}
		if(this.input.downArrow.isPressed()) {this.vy = speed;}
		if(this.input.rightArrow.isPressed()) {this.vx = speed;}
		if(this.input.leftArrow.isPressed()) {this.vx = -speed;}
		this.move();
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
