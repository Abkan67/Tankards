import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;


public class Client extends Thread{
	public static int portConnected = 3000;
	private InetAddress ip; private DatagramSocket socket; private ArrayList<PlayerConnection> allPlayers = new ArrayList<PlayerConnection>();
    public Game game; public boolean running;
	Client(String ip, int port, Game game){ this.game = game; this.running = true;
        try {
	        this.ip = InetAddress.getByName(ip);//Address of the server
            portConnected = port;
	        this.socket = new DatagramSocket();
        }
        catch (SocketException e){closeEverything();} catch (UnknownHostException e){closeEverything();}
	}
	
    public void run() {
        while (running) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace(); closeEverything();
                }
            String incomingMessage = new String(packet.getData());
            System.out.println("Client: Look at that! The Server Sent-> "+ incomingMessage);
            this.handleData(packet);
        }
    }
    private void handleData(DatagramPacket packet) {
    	Protocol.handleServerData(packet.getData(), this);
    }
	private PlayerConnection findPlayer(int ID){
		for(PlayerConnection p: this.allPlayers){
			if(p.getID()==ID) {return p;}
		}
		return null;
	}
    


    public ArrayList<PlayerConnection> getAllPlayers() {return allPlayers;}
    public void sendData(byte[] data){
        DatagramPacket packet = new DatagramPacket(data, data.length, this.ip, portConnected);
        try{socket.send(packet);} catch (IOException e){};
    }
    public static String decodeAddress(String address){
        if(address.charAt(0)!='#'){
            return address;
        }
        else{
            switch(address.substring(1)){
                case "": return "localhost";
                default: return address;
            }
        }
    }
    
	public void closeEverything() {
        this.socket.close(); this.running = false;
	}
    public void addPlayer(Protocol.LOGIN00PACKET p){
        PlayerConnection newPlayer = new PlayerConnection(p.getName(), p.getX(), p.getY(), new Input(), p.getID(), p.isHost());
        allPlayers.add(newPlayer);
    }
    public void disconnect(){
        this.sendData(Protocol.DISCONNECT01PACKET.createPacket(this.game.player.getID()));
    }
    public void disconnect(Protocol.DISCONNECT01PACKET p){
        for(int i = 0; i<this.allPlayers.size(); i++){
            if(p.getID()==allPlayers.get(i).getID()){ if(this.allPlayers.get(i).isHost()){handleHostDisconnect();} this.allPlayers.get(i).disconnect(); this.allPlayers.remove(i); return; }
        }
    }
    private void handleHostDisconnect() {
        JOptionPane.showMessageDialog(Game.currentGame.display,"The Host has Disconnected, Please Join a New Game");
        Game.currentGame.closeEverything(); Game.currentGame.setupGame();
    }
    public void movePlayer(Protocol.MOVE02PACKET p){
        PlayerConnection player = this.findPlayer(p.getID());
        int xSteps = p.getxPos() - player.getX();
        int ySteps = p.getyPos() - player.getY();
        player.move(xSteps, ySteps);
    }
    public void anglePlayer(Protocol.MOUSEMOVE03PACKET p) {
        PlayerConnection player = this.findPlayer(p.getID());
        player.deg = p.getAngle();
    }
    public void anglePlayer(Protocol.MOUSECLICK04PACKET p) {
        PlayerConnection player = this.findPlayer(p.getID());
        player.deg = p.getAngle();
        player.shoot();
    }

    public PlayerConnection loginSelf(Game game, String name, int x, int y, boolean isHost){
		PlayerConnection player = new PlayerConnection(name, x, y, new Input(game), -1, isHost);
		this.sendData(Protocol.LOGIN00PACKET.createPacket(name, x, y, -1, isHost));
        allPlayers.add(player);
        return player;
    }

}
