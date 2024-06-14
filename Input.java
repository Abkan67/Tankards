import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

public class Input implements KeyListener, MouseListener{
    private Game game;
    public InputKey rightArrow = new InputKey();public InputKey leftArrow = new InputKey();
    public InputKey downArrow = new InputKey();public InputKey upArrow = new InputKey();
    public InputKey space = new InputKey();
     public boolean mouseAiming; public Point2D cursorPos = new Point2D.Double();
     
    Input(Game game){
        this.game=game;
        this.game.display.getFrame().addKeyListener(this);this.game.display.getFrame().addMouseListener(this);
    }
    Input(){ /*  For non-local sources*/}
    public static class InputKey{
        private boolean isPressed = false;
        public boolean isPressed () {return this.isPressed;}
        public void changePress(boolean pressed) {this.isPressed = pressed;}
    }
    public void keyPressed(KeyEvent e) {
        this.keyEvent(e.getKeyCode(), true); //handles movement
        if(Game.currentGame.state.equals("connecting")&&Game.currentGame.player.isHost()&&e.getKeyCode() == KeyEvent.VK_ENTER){
            int i = 0;
            for(PlayerConnection player: Game.currentGame.client.getAllPlayers()) {
                Point2D.Double p = getStartingLocation(i++);
                Game.currentGame.client.sendData(Protocol.MOVE02PACKET.createPacket(player.getID(), (int)p.getX(), (int)p.getY()));
            }
            Game.currentGame.client.sendData(Protocol.CHANGESTATE11PACKET.createPacket("playing", Maze.dissembleMaze(Game.currentGame.maze)));
        }
        //if(Game.currentGame.state.equals("victory")&&Game.currentGame.player.isHost()&&e.getKeyCode() == KeyEvent.VK_R) {
            //Game.currentGame.maze = Maze.makeMaze();
            //Game.currentGame.client.sendData(Protocol.CHANGESTATE11PACKET.createPacket("playing", Maze.dissembleMaze(Game.currentGame.maze)));
        //}
    }
    public Point2D.Double getStartingLocation(int i) {
        int height = Game.currentGame.display.getHeight();
        int width = Game.currentGame.display.getWidth();
        switch(i) {
            case 0: return new Point2D.Double(0,0);
            case 1: return new Point2D.Double((double)width-Player.playerDims, (double)height-Player.playerDims);
            case 2: return new Point2D.Double((double)width-Player.playerDims,0);
            case 3: return new Point2D.Double(0,(double)height-Player.playerDims);
            default: return new Point2D.Double(0,0);
        }
    }
    public void keyReleased(KeyEvent e) {
        this.keyEvent(e.getKeyCode(), false);
    }

    private void keyEvent(int keyCode, boolean isPressed) {
        switch(keyCode){
            case KeyEvent.VK_UP: this.upArrow.changePress(isPressed);break;
            case KeyEvent.VK_W: this.upArrow.changePress(isPressed);break;
            case KeyEvent.VK_DOWN: this.downArrow.changePress(isPressed);break;
            case KeyEvent.VK_S: this.downArrow.changePress(isPressed);break;
            case KeyEvent.VK_RIGHT: this.rightArrow.changePress(isPressed);break;
            case KeyEvent.VK_D: this.rightArrow.changePress(isPressed);break;
            case KeyEvent.VK_LEFT: this.leftArrow.changePress(isPressed);break;
            case KeyEvent.VK_A: this.leftArrow.changePress(isPressed);break;
            case KeyEvent.VK_SPACE: this.space.changePress(isPressed); break;
            default: break;
        }
    }

    public void keyTyped(KeyEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(game.player.shoot(game.player.deg))
		    Game.gameClient.sendData(Protocol.MOUSECLICK04PACKET.createPacket(Game.currentGame.player.ID, Game.currentGame.player.deg));
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseAiming = true;
		new Thread(new Runnable () {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(mouseAiming) {
                    if(!Game.currentGame.player.input.space.isPressed()&&game.display.getMousePosition() != null){
					    cursorPos = new Point2D.Double(game.display.getMousePosition().getX(),game.display.getMousePosition().getY());
		                Game.gameClient.sendData(Protocol.CHANGEANGLE03PACKET.createPacket(Game.currentGame.player.ID, cursorPos.getX(), cursorPos.getY()));
                    }
				}
			}
			
		}).start();
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseAiming = false;
	}

}
