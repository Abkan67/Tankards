import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Barrier {
	public Rectangle2D hitbox;
	public BufferedImage[] bis;

	public Barrier(double x, double y, double width, double length) {
		try{
			BufferedImage[] array={
				ImageIO.read(new File("./src/Resources/barrier1.png")),
				ImageIO.read(new File("./src/Resources/barrier2.png")),
				ImageIO.read(new File("./src/Resources/barrier3.png")),
				ImageIO.read(new File("./src/Resources/barrier4.png")),
				ImageIO.read(new File("./src/Resources/barrier5.png"))
			}; 
			bis = array;
		}
		catch (IOException e){// TODO Auto-generated catch block
		e.printStackTrace();}
		
		this.hitbox = new Rectangle2D.Double(x, y, width, length);
	}
	
	public void draw(Graphics2D g) {
		BufferedImage bi = bis[0];
		if (Game.currentGame.barrierHealth.size()>0) {
		switch (Game.currentGame.barrierHealth.get(Game.currentGame.barriers.indexOf(this))) {
		case 4: bi = bis[1];break;
		case 3: bi = bis[2];break;
		case 2: bi = bis[3];break;
		case 1: bi = bis[4];break;
		default: bi = bis[0];
		}
		}
		g.drawImage(bi, (int)hitbox.getX(), (int)hitbox.getY(), (int)hitbox.getMaxX(), (int)hitbox.getMaxY(), 0, 0, 512, 512, null);
	}
}
