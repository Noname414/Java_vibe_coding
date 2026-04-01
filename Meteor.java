import java.awt.*;

/** 垂直下落的隕石，繼承自 Enemy。 */
public class Meteor extends Enemy {
    private double speed;

    /** 隨機位置建構子：從畫面上方隨機位置落下。 */
    public Meteor(int panelWidth) {
        respawn(panelWidth, 0);
    }

    /**
     * 固定座標建構子：將敵人放置於指定中心點，速度為 0（不自行移動），
     * 用於 ArrayList&lt;Enemy&gt; 陣列排列。
     */
    public Meteor(double centerX, double centerY, int size) {
        this.x    = centerX - size / 2.0;
        this.y    = centerY - size / 2.0;
        this.size = size;
        // speed 預設 0.0，不會下落
    }

    @Override
    public void respawn(int panelWidth, int panelHeight) {
        int safeWidth = Math.max(panelWidth, 1);
        this.size  = 20 + rand.nextInt(21);                  // 大小 20–40
        this.x     = rand.nextDouble() * safeWidth;
        this.y     = -size - rand.nextInt(200);              // 從畫面上方外開始
        this.speed = 2 + rand.nextDouble() * 3;             // 速度 2–5 px/frame
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        y += speed;
        if (y - size > panelHeight) {
            respawn(panelWidth, panelHeight);
        }
    }

    @Override
    public double getCenterX() { return x + size / 2.0; }

    @Override
    public double getCenterY() { return y + size / 2.0; }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.fillOval((int) x, (int) y, size, size);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval((int) x + size / 4, (int) y + size / 4, size / 3, size / 3);
    }
}
