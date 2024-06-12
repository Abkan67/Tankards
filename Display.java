import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
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
		if(Game.currentGame.state.equals("victory")) {
			drawVictory(g);
		}
		if(Game.currentGame.state.equals("playing")){
			drawPlaying(g);
		}
	}

	public void drawPlaying(Graphics2D g) {
		g.setColor(Color.black);
		int alivePlayers = 0;

		drawMaze(g, Game.currentGame.maze.getMaze());

		for(PlayerConnection playerConnection: this.game.client.getAllPlayers()){
			playerConnection.update(g);
			alivePlayers+= playerConnection.isAlive?1:0;
		}
		if(alivePlayers<=1) {
			Game.currentGame.state="victory";
		}

	}
	public void drawMaze(Graphics2D g, int[][] maze) {
		for (int r = 0; r < maze.length; r++) {
			for (int c = 0; c < maze[r].length; c++) {
				g.setColor(Color.black);
				if (maze[r][c]==1) {g.fill(new Rectangle2D.Double(getWidth()*c/maze[r].length,
					getHeight()*r/maze.length,getWidth()/maze[r].length,getHeight()/maze.length));}
			}
		}
	}
	public void drawVictory(Graphics2D g) {
		g.setColor(Color.blue);
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
