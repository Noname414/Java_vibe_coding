import java.awt.*;
import java.util.Random;

public class Star {
    private double x;
    private double y;
    private final double speed;
    private final int size;

    public Star(int panelWidth, int panelHeight) {
        Random rand = new Random();
        this.x     = rand.nextDouble() * panelWidth;
        this.y     = rand.nextDouble() * panelHeight;
        this.speed = 1 + rand.nextDouble() * 2;   // 1–3 px/frame
        this.size  = 2 + rand.nextInt(2);          // 2–3 px
    }

    public void update(int panelWidth, int panelHeight) {
        y += speed;
        if (y > panelHeight) {
            y = 0;
            x = new Random().nextDouble() * panelWidth;
        }
    }

    public void draw(Graphics g) {
        g.fillOval((int) x, (int) y, size, size);
    }
}
