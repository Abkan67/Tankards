import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Input implements KeyListener{
    private Game game;
    public InputKey rightArrow = new InputKey();public InputKey leftArrow = new InputKey();
    public InputKey downArrow = new InputKey();public InputKey upArrow = new InputKey();
    Input(Game game){
        this.game=game;
        this.game.display.getFrame().addKeyListener(this);;
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
            case KeyEvent.VK_DOWN: this.downArrow.changePress(isPressed);break;
            case KeyEvent.VK_RIGHT: this.rightArrow.changePress(isPressed);break;
            case KeyEvent.VK_LEFT: this.leftArrow.changePress(isPressed);break;
        }
    }

    public void keyTyped(KeyEvent e) {}

}
