import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Game {
	static Client gameClient;
	static Game currentGame;
	public static int gameWidth = 15;
	public static int gameHeight = 10;
	public Maze maze;
	Display display; Server server; Client client; PlayerConnection player; private Animation animator; private Window windowhandler;
	String state = ""; ArrayList<Barrier> barriers = new ArrayList<>();
	Game() {
		currentGame = this;
		this.display = new Display(this);
		this.windowhandler = new Window(this);
		setupGame();
	}
	public void setupGame(){
		this.state = "enterance";

		String nickname = JOptionPane.showInputDialog(this.display, "What's your name?");
		String port ="", address = "";
		int willRunServer = JOptionPane.showConfirmDialog(this.display, "Host Game", "Join Local Game", JOptionPane.YES_NO_OPTION);
		if(willRunServer==0) {
			this.maze = Maze.makeMaze();
			int serverPort = Server.portConnected;
			server = new Server(serverPort);
			JOptionPane.showMessageDialog(this.display, "Your code is "+serverPort);
			server.start();
			client = new Client("localhost", serverPort, this);
		} else{
			address = "#";JOptionPane.showInputDialog(this.display, "Where is your host? (use # to access presets, or paste in an IP Address)");
			port = "3000";JOptionPane.showInputDialog(this.display, "What is the host's number code/port?");
			client = new Client(Client.decodeAddress(address), Integer.parseInt(port), this);
		}
		
		gameClient = this.client;
		client.start();
		
		this.animator = new Animation(this);
		animator.start();
		
		this.player = this.client.loginSelf(this, nickname, 40, 40, willRunServer==0);

		this.setState("connecting");
	}
	public void tick(){
		this.display.repaint();
	}
	public void closeEverything(){
		this.animator.stop();this.client.closeEverything(); 
		if(this.server!=null){this.server.closeEverything();}
	}
	public void setState(String state){this.state = state;}
	public String getState(){return this.state;}

	public static void main(String[] args) {
		new Game();
	}
	public void setBarriers() {
		this.barriers = new ArrayList<Barrier>();
		int[][] maze = this.maze.getMaze();
		for(int row = 0; row<maze.length; row++){
			for(int col = 0; col<maze[0].length; col++) {
				if(maze[row][col] == 1) {
					this.barriers.add(new Barrier(
						this.display.getWidth()*col/maze[row].length,
						this.display.getHeight()*row/maze.length,
						this.display.getWidth()/maze[row].length,
						this.display.getHeight()/maze.length)
					);
					
				}
			}
		}
		for(int i = 0; i<this.barriers.size(); i++) {
			this.barrierHealth.add(5);
		}
	}

	public ArrayList<Integer> barrierHealth = new ArrayList<Integer>();
    public boolean damageBarrier(int b) {
		barrierHealth.set(b, barrierHealth.get(b)-1);
		if(barrierHealth.get(b)<=0) {
			barriers.remove(b);
			barrierHealth.remove(b);
			return true;
		}
		return false;
    }

}