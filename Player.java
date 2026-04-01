import java.awt.*;

public class Player {
    public double x;
    public double y;
    public final int RADIUS = 20;
    private final double MOVE_SPEED = 6.0;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update(boolean leftHeld, boolean rightHeld,
                       boolean upHeld, boolean downHeld,
                       int panelWidth, int panelHeight) {
        if (leftHeld)  x -= MOVE_SPEED;
        if (rightHeld) x += MOVE_SPEED;
        if (upHeld)    y -= MOVE_SPEED;
        if (downHeld)  y += MOVE_SPEED;

        if (x - RADIUS < 0) x = RADIUS;
        if (panelWidth > 0 && x + RADIUS > panelWidth) x = panelWidth - RADIUS;
        if (panelHeight > 0) {
            if (y - RADIUS < 0) y = RADIUS;
            if (y + RADIUS > panelHeight) y = panelHeight - RADIUS;
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fillOval((int)(x - RADIUS), (int)(y - RADIUS), RADIUS * 2, RADIUS * 2);
    }
}
