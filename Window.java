import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Window implements WindowListener{
    private Game game;
    Window(Game game){
        this.game = game;
        this.game.display.getFrame().addWindowListener(this);
    }
    public void windowActivated(WindowEvent arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'windowActivated'");
    }
    public void windowClosed(WindowEvent arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'windowClosed'");
    }
    public void windowClosing(WindowEvent arg0) {
        if(Game.currentGame.client != null){
            Game.currentGame.client.disconnect();
           // Game.currentGame.closeEverything();
        }
    }
    public void windowDeactivated(WindowEvent arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'windowDeactivated'");
    }
    public void windowDeiconified(WindowEvent arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'windowDeiconified'");
    }
    public void windowOpened(WindowEvent arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'windowOpened'");
    }
    public void windowIconified(WindowEvent arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'windowIconified'");
    }
}
