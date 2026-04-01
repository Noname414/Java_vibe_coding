import java.awt.*;

public class Bullet {
    public double x;
    public double y;
    public final int radius = 5;
    private final double speed = 10;

    public Bullet(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        y -= speed;
    }

    public boolean isOutOfScreen() {
        return y + radius < 0;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
    }
}
