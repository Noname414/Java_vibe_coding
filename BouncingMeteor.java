import java.awt.*;

/** 左右上下反彈的紅色隕石，繼承自 Enemy。 */
public class BouncingMeteor extends Enemy {
    private double velocityX;
    private double velocityY;

    public BouncingMeteor(int panelWidth, int panelHeight) {
        respawn(panelWidth, panelHeight);
    }

    @Override
    public void respawn(int panelWidth, int panelHeight) {
        int safeWidth  = Math.max(panelWidth,  1);
        int safeHeight = Math.max(panelHeight, 1);
        this.size      = 25 + rand.nextInt(26);                            // 大小 25–50
        this.x         = rand.nextDouble() * safeWidth;
        this.y         = rand.nextDouble() * (safeHeight / 2.0);          // 上半部
        this.velocityX = (rand.nextDouble() - 0.5) * 6;                   // -3 ~ 3
        if (Math.abs(this.velocityX) < 1.0) {
            this.velocityX = this.velocityX < 0 ? -1.0 : 1.0;
        }
        this.velocityY = 1 + rand.nextDouble() * 2;                       // 1–3 px/frame
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        x += velocityX;
        y += velocityY;

        if (x - size / 2.0 < 0) {
            x = size / 2.0;
            velocityX = -velocityX;
        } else if (x + size / 2.0 > panelWidth) {
            x = panelWidth - size / 2.0;
            velocityX = -velocityX;
        }

        if (y - size / 2.0 < 0) {
            y = size / 2.0;
            velocityY = -velocityY;
        } else if (y + size / 2.0 > panelHeight) {
            y = panelHeight - size / 2.0;
            velocityY = -velocityY;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(255, 100, 100));
        g2d.fillOval((int)(x - size / 2.0), (int)(y - size / 2.0), size, size);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)(x - size / 2.0), (int)(y - size / 2.0), size, size);
    }
}
