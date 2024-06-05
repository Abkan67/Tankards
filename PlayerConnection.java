import java.awt.Graphics2D;
import java.net.InetAddress;

public class PlayerConnection extends Player{ private static int nextPlayerConnectionID = 0;

	private InetAddress ip; private int port; private boolean isHost = false;
	PlayerConnection(InetAddress ip, int port, String name, int x, int y, Input input, boolean isHost) {//For server use only
		super(x,y, name, input, nextPlayerConnectionID++);
		this.ip = ip; this.port = port; this.isHost = isHost;
	}
	public void update(){}
	public int getPort(){return port;}
	public String getName(){return super.getName();}
	public InetAddress getIP(){return this.ip;}
	public void disconnect(){}

	
	PlayerConnection(String name, int x, int y, Input input, int ID, boolean isHost){//When the client contructs it, handles messages from the Server about what to do                        
		super(x,y, name, input, ID);
		this.isHost = isHost;
	}
	public int getX() {return super.getX();}
	public int getY() {return super.getY();}
	public void update(Graphics2D g){super.update(g); }
	public boolean isHost() {return isHost;}
}
