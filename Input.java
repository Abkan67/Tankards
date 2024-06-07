import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

public class Input implements KeyListener, MouseListener{
    private Game game;
    public InputKey rightArrow = new InputKey();public InputKey leftArrow = new InputKey();
    public InputKey downArrow = new InputKey();public InputKey upArrow = new InputKey(); private boolean mouseAiming; public Point2D cursorPos = new Point2D.Double();
    Input(Game game){
        this.game=game;
        this.game.display.getFrame().addKeyListener(this);this.game.display.getFrame().addMouseListener(this);
    }
    Input(){ // For non-local sources
    }
    public static class InputKey{
        private boolean isPressed = false;
        public boolean isPressed () {return this.isPressed;}
        public void changePress(boolean pressed) {this.isPressed = pressed;}
    }
    public void keyPressed(KeyEvent e) {
        this.keyEvent(e.getKeyCode(), true);
        if(Game.currentGame.state.equals("connecting")&&Game.currentGame.player.isHost()&&e.getKeyCode() == KeyEvent.VK_ENTER){
            Game.currentGame.client.sendData(Protocol.CHANGESTATE11PACKET.createPacket("playing"));
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
		game.player.shoot(game.player.deg);
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
					cursorPos = new Point2D.Double(MouseInfo.getPointerInfo().getLocation().getX(),MouseInfo.getPointerInfo().getLocation().getY());
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
