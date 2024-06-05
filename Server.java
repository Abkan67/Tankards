import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.net.DatagramSocket;
public class Server extends Thread {
	public static int portConnected = 3000;
	private DatagramSocket socket;
	private ArrayList<PlayerConnection> connectedPlayers = new ArrayList<PlayerConnection>();
	private String state = "enterance"; private boolean running;
	Server(int port) {
		this.running = true;
		try{portConnected = port;this.socket = new DatagramSocket(portConnected);} 
		catch (SocketException e) {e.printStackTrace();this.closeEverything();}
	}
	
	public void run() {
		while(running) {
			byte[] data = new byte[80];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {socket.receive(packet);}
			catch(IOException e) {e.printStackTrace();}
			
			String message = new String(packet.getData());
			if(message!=null) {
				System.out.println("Message from Client: "+message);
                this.handlePacket(packet);
			}
		}
	}
	
	public void handlePacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		Protocol.handleData(data, packet.getAddress(), packet.getPort(), this);
	}
	
	public void sendData(byte[] data, InetAddress ip, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
        try{socket.send(packet);} catch (IOException e){closeEverything();};
	}
    public void sendDataToAllCients(byte[] data){
        for(PlayerConnection p: this.connectedPlayers){this.sendData(data, p.getIP(), p.getPort());}
    }
	
	public void closeEverything() {
		this.socket.close(); this.running = false;
	}

	private PlayerConnection findPlayer(int ID){
		for(PlayerConnection p: this.connectedPlayers){
			if(p.getID()==ID) {return p;}
		}
		return null;
	}
	public void connectPlayer(Protocol.LOGIN00PACKET p) {
		p.sendAllPreviouslyConnectedPlayers(this, this.connectedPlayers);
		PlayerConnection newPlayer = new PlayerConnection(p.getIP(), p.getPort(), p.getName(), p.getX(), p.getY(), new Input(), p.isHost());
		p.setID(newPlayer.getID());
		sendDataToAllCients(Protocol.LOGIN00PACKET.createPacket(p.getName(), p.getX(), p.getY(), p.getID(), p.isHost()));
		this.connectedPlayers.add(newPlayer);
		sendData(("10"+newPlayer.getID()+";").getBytes(), p.getIP(), p.getPort());
	}
	public void disconnectPlayer(Protocol.DISCONNECT01PACKET disconnectPacket) {
		for(int i =0; i<this.connectedPlayers.size(); i++){
			PlayerConnection connection = this.connectedPlayers.get(i);
			if(connection.getID()==disconnectPacket.getID()){this.connectedPlayers.get(i).disconnect();this.connectedPlayers.remove(i); return;}
		}
	}
	public void movePlayer(Protocol.MOVE02PACKET p){
		PlayerConnection player = findPlayer(p.getID());
		if (player==null) {return;}
		int xSteps = p.getxPos() - player.getX();
		int ySteps = p.getyPos() - player.getY();
		player.move(xSteps, ySteps);
		sendDataToAllCients(p.getDataToSendToClients());
	}
	public void setState(String state){
		this.state = state;
	}
	public ArrayList<PlayerConnection> getConnectedPlayers(){return this.connectedPlayers;}
	
	

}
