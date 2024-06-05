import javax.swing.JOptionPane;

public class Game {
	static Client gameClient;
	static Game currentGame;
	Display display; Server server; Client client; PlayerConnection player; private Animation animator; private Window windowhandler;
	String state = "";// private Maze maze;
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
			int serverPort = Server.portConnected;
			server = new Server(serverPort);
			JOptionPane.showMessageDialog(this.display, "Your code is "+serverPort);
			server.start();
			client = new Client("localhost", serverPort, this);
		} else{
			address = JOptionPane.showInputDialog(this.display, "Where is your host? (use # to access presets)");
			port = JOptionPane.showInputDialog(this.display, "What is the host's number code?");
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
//	public Maze getMaze(){return maze;}
	public void setState(String state){this.state = state;}

	public static void main(String[] args) {
		new Game();
	}

}
//Procedural maps