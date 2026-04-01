import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel implements MouseListener {

    private static final int MAX_BULLETS = 3;

    // ── 敵人陣列：3 排 × 8 隻，共 24 個 Enemy ──
    private static final int    ENEMY_ROWS      = 3;
    private static final int    ENEMY_COLS      = 8;
    private static final int    ENEMY_SIZE      = 30;   // 直徑（px）
    private static final int    ENEMY_COL_GAP   = 90;  // 欄間距（中心到中心）
    private static final int    ENEMY_ROW_GAP   = 70;  // 列間距（中心到中心）
    private static final int    ENEMY_START_X   = (800 - (ENEMY_COLS - 1) * ENEMY_COL_GAP) / 2; // = 85
    private static final int    ENEMY_START_Y   = 80;  // 第一列中心 Y
    // ── 整排移動參數 ──
    private static final double ENEMY_SPEED     = 1.5; // 每幀水平位移（px）
    private static final double ENEMY_STEP_DOWN = 30;  // 碰邊後整排下移距離（px）
    /** 目前水平移動方向：+1.5 = 向右，-1.5 = 向左 */
    private double enemyDirX = ENEMY_SPEED;

    private final GameState       state   = new GameState();
    private Player                player;
    private final ArrayList<Enemy>  enemies = new ArrayList<>();
    private final ArrayList<Star>   stars   = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    private boolean leftKeyHeld;
    private boolean rightKeyHeld;
    private boolean upKeyHeld;
    private boolean downKeyHeld;
    private boolean spaceKeyHeld;

    private javax.swing.Timer gameTimer;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addMouseListener(this);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_LEFT  -> leftKeyHeld  = true;
                    case KeyEvent.VK_RIGHT -> rightKeyHeld = true;
                    case KeyEvent.VK_UP    -> upKeyHeld    = true;
                    case KeyEvent.VK_DOWN  -> downKeyHeld  = true;
                    case KeyEvent.VK_SPACE -> {
                        if (!spaceKeyHeld) {
                            spaceKeyHeld = true;
                            shootBullet();
                        }
                    }
                    case KeyEvent.VK_P -> {
                        if (!state.gameOver) state.paused = !state.paused;
                    }
                    case KeyEvent.VK_R -> resetGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_LEFT  -> leftKeyHeld  = false;
                    case KeyEvent.VK_RIGHT -> rightKeyHeld = false;
                    case KeyEvent.VK_UP    -> upKeyHeld    = false;
                    case KeyEvent.VK_DOWN  -> downKeyHeld  = false;
                    case KeyEvent.VK_SPACE -> spaceKeyHeld = false;
                }
            }
        });

        for (int i = 0; i < 50; i++) {
            stars.add(new Star(800, 600));
        }

        player = new Player(400, 520);
        spawnEnemies();

        gameTimer = new javax.swing.Timer(16, e -> {
            updateGame();
            repaint();
        });
        gameTimer.start();

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void updateGame() {
        if (state.gameOver || state.paused) return;

        state.frameCount++;

        int w = getWidth();
        int h = getHeight();

        player.update(leftKeyHeld, rightKeyHeld, upKeyHeld, downKeyHeld, w, h);

        for (Star star : stars) {
            star.update(w, h);
        }

        updateEnemies();
        updateBullets();
        checkBulletCollisions();
        checkPlayerCollisions();

        // 所有敵人消滅後重新生成陣列
        if (enemies.isEmpty()) {
            spawnEnemies();
        }
    }

    private void shootBullet() {
        if (state.gameOver || bullets.size() >= MAX_BULLETS) return;
        bullets.add(new Bullet(player.x, player.y - player.RADIUS));
    }

    private void updateBullets() {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();
            if (b.isOutOfScreen()) it.remove();
        }
    }

    /**
     * 整排左右移動：每幀水平位移 enemyDirX，
     * 偵測到任何一隻碰到左/右邊界時，整排下移 ENEMY_STEP_DOWN px 並反向。
     */
    private void updateEnemies() {
        int w = getWidth();

        // 1. 整排水平位移
        for (Enemy e : enemies) {
            e.move(enemyDirX, 0);
        }

        // 2. 偵測是否任一敵人碰到左/右邊界
        boolean hitEdge = false;
        for (Enemy e : enemies) {
            double leftEdge  = e.getCenterX() - e.getRadius();
            double rightEdge = e.getCenterX() + e.getRadius();
            if (leftEdge <= 0 || rightEdge >= w) {
                hitEdge = true;
                break;
            }
        }

        // 3. 碰邊 → 整排下移一格，水平方向反向
        if (hitEdge) {
            enemyDirX = -enemyDirX;
            for (Enemy e : enemies) {
                e.move(0, ENEMY_STEP_DOWN);
            }
        }
    }

    /**
     * 產生 ENEMY_ROWS × ENEMY_COLS 排敵人，座標整齊排列。
     * 使用固定座標建構子 Meteor(centerX, centerY, size)。
     */
    private void spawnEnemies() {
        enemies.clear();
        enemyDirX = ENEMY_SPEED; // 重設為向右出發
        for (int row = 0; row < ENEMY_ROWS; row++) {
            for (int col = 0; col < ENEMY_COLS; col++) {
                double cx = ENEMY_START_X + col * ENEMY_COL_GAP;
                double cy = ENEMY_START_Y + row * ENEMY_ROW_GAP;
                enemies.add(new Meteor(cx, cy, ENEMY_SIZE));
            }
        }
    }

    private void checkBulletCollisions() {
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            boolean hit = false;

            Iterator<Enemy> enemyIt = enemies.iterator();
            while (enemyIt.hasNext()) {
                Enemy e = enemyIt.next();
                if (hits(b, e)) {
                    enemyIt.remove();   // 敵人被打中 → 從陣列移除
                    state.score += 10;
                    hit = true;
                    break;              // 一顆子彈只打到一個敵人
                }
            }

            if (hit) bulletIt.remove();
        }
    }

    private boolean hits(Bullet b, Enemy enemy) {
        double dx   = b.x - enemy.getCenterX();
        double dy   = b.y - enemy.getCenterY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist <= b.radius + enemy.getRadius();
    }

    private void checkPlayerCollisions() {
        for (Enemy e : enemies) {
            if (overlaps(player, e)) {
                state.gameOver = true;
                return;
            }
        }
    }

    private boolean overlaps(Player p, Enemy enemy) {
        double dx   = p.x - enemy.getCenterX();
        double dy   = p.y - enemy.getCenterY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < p.RADIUS + enemy.getRadius();
    }

    private void resetGame() {
        int w = Math.max(getWidth(),  1);
        int h = Math.max(getHeight(), 1);

        state.reset();

        leftKeyHeld = rightKeyHeld = upKeyHeld = downKeyHeld = spaceKeyHeld = false;

        player = new Player(w / 2.0, h - 80.0);
        bullets.clear();
        spawnEnemies();

        if (!gameTimer.isRunning()) gameTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        for (Star star : stars) star.draw(g2d);

        // 繪製所有敵人
        for (Enemy e : enemies) e.draw(g2d);

        for (Bullet b : bullets) b.draw(g2d);

        player.draw(g2d);

        drawHUD(g2d);

        if (state.gameOver) drawGameOver(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.drawString("Frame: " + state.frameCount, 16, 28);
        g2d.drawString("Score: " + state.score,      16, 54);

        String status = state.gameOver ? "Status: GAME OVER"
                      : (state.paused  ? "Status: PAUSED"
                                       : "Status: RUNNING");
        g2d.drawString(status,               16, 80);
        g2d.drawString("P: Pause  R: Restart", 16, 106);
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int tx = (getWidth()  - fm.stringWidth(title)) / 2;
        int ty = (getHeight() - fm.getHeight())        / 2 + fm.getAscent();
        g2d.drawString(title, tx, ty);

        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreText = "Score: " + state.score;
        fm = g2d.getFontMetrics();
        g2d.drawString(scoreText, (getWidth() - fm.stringWidth(scoreText)) / 2, ty + 50);
    }

    @Override public void mouseClicked(MouseEvent e)  { }
    @Override public void mousePressed(MouseEvent e)  { requestFocusInWindow(); }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e)  { }
    @Override public void mouseExited(MouseEvent e)   { }
}
