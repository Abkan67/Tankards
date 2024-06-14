import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Display extends JComponent{
	
	private Game game; private JFrame frame;
	Display(Game game) {
		this.game=game;
		this.frame = new JFrame();
		this.frame.setSize(850,650);
		this.frame.setResizable(false);
		this.frame.setTitle("Untitles Tank Game");
		this.frame.setDefaultCloseOperation(3);
		this.frame.setVisible(true);
		this.frame.add(this);



	}
	
	public JFrame getFrame(){return this.frame;}
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		if(Game.currentGame.state.equals("enterance")){
			this.drawEnterance(g);
		}
		if(Game.currentGame.state.equals("connecting")){
			this.drawConnecting(g);
		}
		if(Game.currentGame.state.equals("victory")) {
			drawPlaying(g);
			drawVictory(g);
		}
		if(Game.currentGame.state.equals("playing")){
			drawPlaying(g);
			if(!Game.currentGame.player.inRadarMode) {
				drawBlindness(g);
			}
			if(Game.currentGame.player.input.space.isPressed()) {
				drawBar(g);
			}
		}
	}

	public void drawPlaying(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		int alivePlayers = 0;

		drawMaze(g, Game.currentGame.maze.getMaze());

		for(PlayerConnection playerConnection: this.game.client.getAllPlayers()){
			playerConnection.update(g);
			alivePlayers+= playerConnection.isAlive?1:0;
		}
		if(alivePlayers<=1 && Game.currentGame.client.getAllPlayers().size()>1) {
			Game.currentGame.state="victory";
			
		}

	}
	public void drawMaze(Graphics2D g, int[][] maze) {
		g.setColor(new Color(74,71,51));
		for(int i = 0; i < this.game.barriers.size(); i++) {
			this.game.barriers.get(i).draw(g);
		}
		g.setColor(Color.BLACK);
	}
	public void drawBlindness(Graphics2D g) {
		g.setColor(Color.black);
		Area blind = new Area(new Rectangle(0,0,1000,1000));
		Player p = Game.currentGame.player;
		blind.subtract(new Area(new Ellipse2D.Double(p.getX()-Player.playerDims, p.getY()-Player.playerDims, 
		Player.playerDims*3.0, Player.playerDims*3.0)));
		g.fill(blind);
	}
	public void drawBar(Graphics2D g) {
		g.setColor(Color.white);
		g.fill(new Rectangle(Game.currentGame.player.getX() - Player.playerDims/2, 
		Game.currentGame.player.getY()+100, Player.playerDims*2, 10));
		g.setColor(Color.green);
		g.fill(new Rectangle(Game.currentGame.player.getX() - Player.playerDims/2, Game.currentGame.player.getY()+100,
		 (int)(Math.min(500, 500-Game.currentGame.player.changeStateTimer) * (Player.playerDims/250.0)),10));
	}
	public void drawVictory(Graphics2D g) {
		g.setColor(Color.white);
		g.fillRect(50,50,100,50);
		for(PlayerConnection playerConnection: this.game.client.getAllPlayers()){
			if(playerConnection.isAlive) {
				playerConnection.draw(g);
				g.setColor(Color.black);
				g.drawString(playerConnection.getName()+" has won!!!", 50, 70);
			}
		}
	}
	public void drawConnecting(Graphics2D g) {
		g.setColor(new Color(50,150,0));
		g.fillRect(0,0, getWidth(), getHeight());
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(1));
		g.fillRect(getWidth()/2-50, 50, 100,40);
		g.fillRect(getWidth()/2-50, 100, 100,40);
		g.fillRect(getWidth()/2-50, 150, 100,40);
		g.fillRect(getWidth()/2-50, 200, 100,40);
		for(int i = 0; i<Game.currentGame.client.getAllPlayers().size(); i++){
			g.setColor(Color.green);
			if(Game.currentGame.client.getAllPlayers().get(i).isHost()){g.setColor(Color.red);}
			g.fillRect(getWidth()/2-50, i*50+50, 100, 40);
			g.setColor(Color.black);
			g.drawString(Game.currentGame.client.getAllPlayers().get(i).getName(), getWidth()/2-45, i*50+60);
		}
		if(Game.currentGame.player.isHost()){
			g.setColor(Color.black);
			g.drawString("Press ENTER to start", getWidth()/2-60, 275);
		}
		
	}
	public void drawEnterance(Graphics2D g){
		g.setColor(Color.blue);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.white);
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 40));
		g.drawString("T A N K A R D S", 300, 100);
	}


}
