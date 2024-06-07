import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Display extends JComponent{
	
	private Game game; private JFrame frame;
	Display(Game game) {
		this.game=game;
		this.frame = new JFrame();
		this.frame.setSize(400,400);
		this.frame.setTitle("Untitles Tank Game");
		this.frame.setDefaultCloseOperation(3);
		this.frame.add(this);
		this.frame.setVisible(true);
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
		if(Game.currentGame.state.equals("playing")){
			g.setColor(Color.black);
			
			for(PlayerConnection playerConnection: this.game.client.getAllPlayers()){
				playerConnection.update(g);
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
			g.drawString("Press ENTER to start", getWidth()/2-100, 250);
		}
		
	}
	public void drawEnterance(Graphics2D g){
		g.setColor(Color.blue);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.white);
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 40));
		g.drawString("T A N K A R D S", 0, 100);
	}


}
