import java.awt.*;
import java.util.Random;

public abstract class Enemy {
    protected double x;
    protected double y;
    protected int size;
    protected final Random rand = new Random();

    public abstract void update(int panelWidth, int panelHeight);
    public abstract void respawn(int panelWidth, int panelHeight);
    public abstract void draw(Graphics2D g2d);

    /** 整排平移時由 GamePanel 呼叫，直接位移 (dx, dy)。 */
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public double getCenterX() { return x; }
    public double getCenterY() { return y; }
    public int getRadius()     { return size / 2; }
}
