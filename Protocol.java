

import java.net.InetAddress;
import java.util.ArrayList;
public class Protocol {
	public static void handleData(byte[] data, InetAddress address, int port, Server server) {
	//When the server gets data
		String type = findMessageType(data);
		if(type.equals("LOGIN")) {
			LOGIN00PACKET p = new LOGIN00PACKET(data, address, port);
			System.out.println(p.getConnectInfoStatment());
			server.connectPlayer(p);
		}
		if(type.equals("DISCONNECT")){
			DISCONNECT01PACKET p = new DISCONNECT01PACKET(data, address, port);
			System.out.println(p.getInfoStatement());
			server.disconnectPlayer(p);
			server.sendDataToAllCients(Protocol.DISCONNECT01PACKET.createPacket(p.getID()));
		}
		if(type.equals("MOVE")){
			MOVE02PACKET p = new MOVE02PACKET(data, address, port);
			server.movePlayer(p);
		}
		if(type.equals("CHANGESTATE")){
			CHANGESTATE11PACKET p = new CHANGESTATE11PACKET(data, address, port);
			server.setState(p.getState());
			server.sendDataToAllCients(p.getData());
		}
		if(type.equals("CHANGEANGLE")) {
			CHANGEANGLE03PACKET p = new CHANGEANGLE03PACKET(data, address, port);
			server.changeAnglePlayer(p);
		}
		if(type.equals("MOUSECLICK")) {
			MOUSECLICK04PACKET p = new MOUSECLICK04PACKET(data, address, port);
			server.mouseclickPlayer(p);
		}
	}
	
	public static void handleServerData(byte[] data, Client client) {
	//When a client gets data
		String type = findMessageType(data);
		if(type.equals("LOGIN")){
			LOGIN00PACKET p = new LOGIN00PACKET(data);
			System.out.println(p.getHasConnectedInfoStatement());
			client.addPlayer(p);
		}
		if(type.equals("CONFIRMLOGIN")){
			CONFIRMLOGIN10PACKET p = new CONFIRMLOGIN10PACKET(data);
			client.game.player.setID(p.getID());
		}
		if(type.equals("MOVE")){
			MOVE02PACKET p = new MOVE02PACKET(data);
			//if(!Game.currentGame.getState().equals("playing") || p.getID() != Game.currentGame.player.getID()) 
			client.movePlayer(p);
		}
		if(type.equals("DISCONNECT")){
			DISCONNECT01PACKET p = new DISCONNECT01PACKET(data);
			client.disconnect(p);
		}
		if(type.equals("CHANGESTATE")){
			CHANGESTATE11PACKET p = new CHANGESTATE11PACKET(data);
			Game.currentGame.setState(p.getState());
			if(p.getState().equals("playing")){
				Game.currentGame.maze = Maze.createMaze(p.getOtherInfo());
				Game.currentGame.setBarriers();
			}
		}
		if(type.equals("CHANGEANGLE")) {
			CHANGEANGLE03PACKET p = new CHANGEANGLE03PACKET(data);
			//if(p.getID() != Game.currentGame.player.getID())
			client.anglePlayer(p);
			
		}
		if(type.equals("MOUSECLICK")) {
			MOUSECLICK04PACKET p = new MOUSECLICK04PACKET(data);
			if(p.getID() != Game.currentGame.player.getID()) 
			client.playerShoot(p);
			
		}
	}
	


	private static String findMessageType(byte[] data) {
		String type = new String(data).substring(0,2);
		switch(type) {
		case "00": return "LOGIN";
		case "01": return "DISCONNECT";
		case "02": return "MOVE";
		case "03": return "CHANGEANGLE";
		case "04": return "MOUSECLICK";
		case "10": return "CONFIRMLOGIN";
		case "11": return "CHANGESTATE";
		default: return "INVALID";
		}
		
	}
	
		//Packets will be in charge of packing and reconstructing themselves
		private static String getFormattedData(byte[] data){return new String(data).trim().substring(2)+";";}

		static class LOGIN00PACKET {
			public static byte[] createPacket(String name, int x, int y, int ID, boolean isHost){
				return ("00"+name+";"+x+";"+y+";"+ID+";"+ (isHost ? 0:1)+";").getBytes();
			}
			private int port; private InetAddress address; private byte[] data; 
			private String name; private int x; private int y; private int ID; 
			private boolean isHost;
			
			LOGIN00PACKET(byte[] data, InetAddress address, int port) { //Constructed when the server receives data
				this.port = port; this.address = address; this.data=data; String[] allData = getFormattedData(data).split(";"); this.name = allData[0]; 
				this.x = Integer.parseInt(allData[1]); this.y=Integer.parseInt(allData[2]); this.isHost = (allData[4].equals("0"));
			}
			public String getName() {return name;}
			public int getX(){return x;}
			public int getY(){return y;}
			public int getID(){return ID;}
			public void setID(int ID){this.ID = ID;}
			public boolean isHost() {return isHost;}
			public InetAddress getIP() {return address;}
			public int getPort() {return port;}

			//Stuff sent from the Connecting Client to the Server
			public String getConnectInfoStatment() {
				return "["+this.address.getHostAddress()+": "+this.port+"] "+this.name+" is trying to connect";
			}

			//Stuff the server sends to the Client afterwards
			public void sendAllPreviouslyConnectedPlayers(Server server, ArrayList<PlayerConnection> playerConnections){
				for(PlayerConnection p: playerConnections){
					server.sendData(createPacket(p.getName(), p.getX(), p.getY(), p.getID(), p.isHost()), this.address, this.port);
				}
			}
			LOGIN00PACKET(byte[] data){
				this.data=data; String[] allData = getFormattedData(data).split(";");
				this.name = allData[0]; 
				this.x = Integer.parseInt(allData[1]);
				this.y = Integer.parseInt(allData[2]);
				this.ID = Integer.parseInt(allData[3].trim());
				this.isHost = (allData[4].equals("0"));

			}
			public String getHasConnectedInfoStatement() {return this.name + " has connected";}

			
		}
		static class CONFIRMLOGIN10PACKET{
			private int ID;
			CONFIRMLOGIN10PACKET(byte[] data){String[] allData = getFormattedData(data).split(";"); this.ID = Integer.parseInt(allData[0]);}
			public int getID(){return this.ID;}
		}
		static class CHANGESTATE11PACKET{
			private InetAddress address; private int port; private String state; private byte[] data;
			private String otherInfo;
			public static byte[] createPacket(String state){ return ("11"+findState(state)+";").getBytes();}
			public static byte[] createPacket(String state, String otherInfo){ return ("11"+findState(state)+";"+otherInfo+";").getBytes();}
			private static String findState(int state){switch(state){case 1: return "LOAD"; case 2: return "playing";default:return "INVALID";}}
			private static int findState(String state){switch(state){case "playing": return 2; case "LOAD": return 1; default: return 0;}}

			public CHANGESTATE11PACKET(byte[] data, InetAddress address, int port){
				this.port=port; this.address = address; this.data = data;
				String[] allData = getFormattedData(data).split(";");
				this.state = findState(Integer.parseInt(allData[0]));
				if(allData.length >1){
					otherInfo = allData[1];
				}
			}
			public String getState(){return state;}
			public byte[] getData(){return this.data;}
			public String getOtherInfo() {return this.otherInfo;}

			public CHANGESTATE11PACKET(byte[] data){
				this.data = data;
				String[] allData = getFormattedData(data).split(";");
				this.state = findState(Integer.parseInt(allData[0]));
				if(allData.length >1){
					otherInfo = allData[1];
				}
			}
		}
		static class DISCONNECT01PACKET {
			private InetAddress address; private int port; private int ID;
			public DISCONNECT01PACKET(byte[] data, InetAddress address, int port){
				this.port = port; this.address=address;
				String[] allData = getFormattedData(data).split(";");
				this.ID = Integer.parseInt(allData[0]);
			}
			public int getID() {return this.ID;}
			public String getInfoStatement() {return "id "+this.ID+" wants to disconnect";}
			public byte[] getDataToSend(String name, int ID) {return ("01"+ID+name).getBytes(); }
			public void sendDataToServer(Client client, String name, int ID){client.sendData(this.getDataToSend(name, ID));}
			public void sendDataToClient(Server server){}

			public DISCONNECT01PACKET(byte[] data){
				String[] allData = getFormattedData(data).split(";");
				this.ID = Integer.parseInt(allData[0]);
			}

			public static byte[] createPacket(int ID){return ( "01"+ID+";" ).getBytes();} 
		}
		static class MOVE02PACKET {
			private int port; private InetAddress address; private byte[] data; private int ID; private int xPos; private int yPos;
			public InetAddress getIP() {return this.address;}
			public int getPort() {return this.port;}
			public int getxPos() {return this.xPos;}
			public int getyPos() {return this.yPos;}
			public int getID() {return this.ID;}

			MOVE02PACKET(byte[] data, InetAddress address, int port){
				this.port = port; this.address=address; this.data=data; String[] allData = getFormattedData(this.data).split(";");
				this.ID = Integer.parseInt(allData[0]);
				this.xPos = Integer.parseInt(allData[1]);
				this.yPos = Integer.parseInt(allData[2]);
			}
			public byte[] getDataToSendToClients() {return ( "02"+this.ID+";"+this.xPos+";"+this.yPos+";" ).getBytes();}

			MOVE02PACKET(byte[] data){
				String[] allData = getFormattedData(data).split(";");
				this.ID = Integer.parseInt(allData[0]);
				this.xPos = Integer.parseInt(allData[1]);
				this.yPos = Integer.parseInt(allData[2]);
			}

			public static byte[] createPacket(int ID, int x, int y){
				return ( "02"+ID+";"+x+";"+y+";" ).getBytes();
			}
		}
		static class MOUSECLICK04PACKET {
			private int port; private InetAddress address; private byte[] data; private int ID;
			private double angle;
			public InetAddress getIP() {return this.address;}
			public int getPort() {return this.port;}
			public int getID() {return this.ID;}
			public double getAngle() {return this.angle;}

			
			public MOUSECLICK04PACKET (byte[] data, InetAddress address, int port) {
				this.port = port; this.address=address; this.data=data; String[] allData = getFormattedData(this.data).split(";");
				this.ID = Integer.parseInt(allData[0]);
				this.angle = Double.parseDouble(allData[1]);
				
			}
			public byte[] getDataToSendToClients() {return ("04"+this.ID+";"+this.angle+";").getBytes();}
			
			MOUSECLICK04PACKET (byte[] data) {
				String[] allData = getFormattedData(data).split(";");
				this.ID = Integer.parseInt(allData[0]);
				this.angle = Double.parseDouble(allData[1]);
			}
			public static byte[] createPacket(int ID, double angle) {
				return ("04"+ID+";"+angle+";").getBytes();
			}
		}
		static class CHANGEANGLE03PACKET {
			private int port; private InetAddress address; private byte[] data; private int ID;
			private double xVal, yVal;
			public InetAddress getIP() {return this.address;}
			public int getPort() {return this.port;}
			public int getID() {return this.ID;}
			public double getX() {return xVal;}
			public double getY() {return yVal;}
			
			public CHANGEANGLE03PACKET (byte[] data, InetAddress address, int port) {
				this.port = port; this.address=address; this.data=data; String[] allData = getFormattedData(this.data).split(";");
				this.ID = Integer.parseInt(allData[0]);
				this.xVal = Double.parseDouble(allData[1]);
				this.yVal = Double.parseDouble(allData[2]);
				
			}
			public byte[] getDataToSendToClients() {return ("03"+this.ID+";"+this.xVal+";"+this.yVal+";").getBytes();}
			
			CHANGEANGLE03PACKET (byte[] data) {
				String[] allData = getFormattedData(data).split(";");
				this.ID = Integer.parseInt(allData[0]);
				this.xVal = Double.parseDouble(allData[1]);
				this.yVal = Double.parseDouble(allData[2]);
			}
			public static byte[] createPacket(int ID, double xVal, double yVal) {
				return ("03"+ID+";"+xVal+";"+yVal+";").getBytes();
			}
			
		}

}
