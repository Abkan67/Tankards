
public class Animation implements Runnable {
    private boolean running = true;
    private Thread thread;
    private Game game; 
    Animation(Game game){
        this.game=game;
    }

    public synchronized void start() {this.running = true; this.thread = new Thread(this);this.thread.start();}

    public synchronized void stop() {
        this.running = false;

    }
    public void run() {
        while(running){
        this.game.tick();
        try {Thread.sleep(4);} catch (InterruptedException e) { e.printStackTrace(); }
    }}
}
