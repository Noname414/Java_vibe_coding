public class GameState {
    public long frameCount = 0;
    public int score = 0;
    public boolean gameOver = false;
    public boolean paused = false;

    public void reset() {
        frameCount = 0;
        score = 0;
        gameOver = false;
        paused = false;
    }
}
