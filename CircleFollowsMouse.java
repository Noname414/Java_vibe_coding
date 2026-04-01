import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class CircleFollowsMouse extends JFrame {
    private CirclePanel circlePanel;

    public CircleFollowsMouse() {
        // 設定視窗標題
        setTitle("Circle - Keyboard Control");
        
        // 設定視窗大小
        setSize(800, 600);
        
        // 設定關閉視窗時結束程式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 設定視窗置中
        setLocationRelativeTo(null);
        
        // 建立並加入圓形面板
        circlePanel = new CirclePanel();
        add(circlePanel);
        
        // 顯示視窗
        setVisible(true);
    }

    // 星點類別
    class Star {
        double x;
        double y;
        double speed;
        int size;

        public Star(int panelWidth, int panelHeight) {
            Random rand = new Random();
            this.x = rand.nextDouble() * panelWidth;
            this.y = rand.nextDouble() * panelHeight;
            this.speed = 1 + rand.nextDouble() * 2; // 速度 1-3 像素/幀
            this.size = 2 + rand.nextInt(2); // 大小 2-3 像素
        }

        public void update(int panelHeight) {
            y += speed;
            // 當星點移出底部時，重新從頂部出現
            if (y > panelHeight) {
                y = 0;
                Random rand = new Random();
                x = rand.nextDouble() * 800;
            }
        }

        public void draw(Graphics g) {
            g.fillOval((int)x, (int)y, size, size);
        }
    }

    // 隕石類別（垂直下落）
    class Meteor {
        double x;
        double y;
        double speed;
        int size;
        private final Random rand = new Random();

        public Meteor(int panelWidth) {
            respawn(panelWidth);
        }

        private void respawn(int panelWidth) {
            int safeWidth = Math.max(panelWidth, 1);
            this.size = 20 + rand.nextInt(21); // 大小 20-40
            this.x = rand.nextDouble() * safeWidth;
            this.y = -size - rand.nextInt(200); // 從畫面上方外開始
            this.speed = 2 + rand.nextDouble() * 3; // 速度 2-5 像素/幀
        }

        public void update(int panelWidth, int panelHeight) {
            y += speed;
            // 掉出底部後回到上方隨機位置
            if (y - size > panelHeight) {
                respawn(panelWidth);
            }
        }

        public void hitAndRespawn(int panelWidth) {
            respawn(panelWidth);
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.GRAY);
            g2d.fillOval((int) x, (int) y, size, size);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval((int) x + size / 4, (int) y + size / 4, size / 3, size / 3);
        }
    }

    // 反彈球體類別（左右反彈）
    class BouncingMeteor {
        double x;
        double y;
        double velocityX; // 水平速度
        double velocityY; // 垂直速度
        int size;
        private final Random rand = new Random();

        public BouncingMeteor(int panelWidth, int panelHeight) {
            respawn(panelWidth, panelHeight);
        }

        public void respawn(int panelWidth, int panelHeight) {
            int safeWidth = Math.max(panelWidth, 1);
            int safeHeight = Math.max(panelHeight, 1);
            this.size = 25 + rand.nextInt(26); // 大小 25-50
            this.x = rand.nextDouble() * safeWidth;
            this.y = rand.nextDouble() * (safeHeight / 2.0); // 在上半部隨機位置
            this.velocityX = (rand.nextDouble() - 0.5) * 6; // 水平速度 -3 到 3
            if (Math.abs(this.velocityX) < 1.0) {
                this.velocityX = this.velocityX < 0 ? -1.0 : 1.0;
            }
            this.velocityY = 1 + rand.nextDouble() * 2; // 垂直速度 1-3 像素/幀
        }

        public void update(int panelWidth, int panelHeight) {
            // 更新位置
            x += velocityX;
            y += velocityY;

            // 左右邊界碰撞反彈
            if (x - size / 2 < 0) {
                x = size / 2.0;
                velocityX = -velocityX; // 反向移動
            } else if (x + size / 2 > panelWidth) {
                x = panelWidth - size / 2.0;
                velocityX = -velocityX; // 反向移動
            }

            // 上下邊界碰撞反彈
            if (y - size / 2 < 0) {
                y = size / 2.0;
                velocityY = -velocityY;
            } else if (y + size / 2 > panelHeight) {
                y = panelHeight - size / 2.0;
                velocityY = -velocityY;
            }
        }

        public void draw(Graphics2D g2d) {
            g2d.setColor(new Color(255, 100, 100)); // 紅色隕石
            g2d.fillOval((int)(x - size / 2), (int)(y - size / 2), size, size);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval((int)(x - size / 2), (int)(y - size / 2), size, size);
        }
    }

    // 子彈類別（向上飛行）
    class Bullet {
        double x;
        double y;
        int radius;
        double speed;

        public Bullet(double startX, double startY) {
            this.x = startX;
            this.y = startY;
            this.radius = 5;
            this.speed = 10;
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

    // 內部類別：繪製圓形的面板
    class CirclePanel extends JPanel implements MouseListener {
        // 紅球當前位置（使用 double 以支援平滑移動）
        private double circleX = 400;
        private double circleY = 300;
        
        private final int CIRCLE_RADIUS = 20;
        /** 按住方向鍵時每幀移動像素 */
        private final double MOVE_SPEED = 6.0;
        /** 畫面上同時存在的子彈上限 */
        private static final int MAX_BULLETS = 3;
        private boolean leftKeyHeld;
        private boolean rightKeyHeld;
        private boolean upKeyHeld;
        private boolean downKeyHeld;
        /** 避免按住空白鍵時系統重複 keyPressed 連發 */
        private boolean spaceKeyHeld;
        
        // 星點列表
        private ArrayList<Star> stars;
        private ArrayList<Bullet> bullets;
        private Meteor meteor;
        private BouncingMeteor bouncingMeteor;
        private long frameCount = 0;
        private int score = 0;
        private javax.swing.Timer gameTimer;
        private boolean gameOver = false; // 遊戲結束狀態
        private boolean paused = false; // 暫停狀態

        public CirclePanel() {
            // 設定黑色背景
            setBackground(Color.BLACK);
            setFocusable(true);
            addMouseListener(this);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_LEFT) {
                        leftKeyHeld = true;
                    } else if (code == KeyEvent.VK_RIGHT) {
                        rightKeyHeld = true;
                    } else if (code == KeyEvent.VK_UP) {
                        upKeyHeld = true;
                    } else if (code == KeyEvent.VK_DOWN) {
                        downKeyHeld = true;
                    } else if (code == KeyEvent.VK_SPACE) {
                        if (!spaceKeyHeld) {
                            spaceKeyHeld = true;
                            shootBullet();
                        }
                    } else if (code == KeyEvent.VK_P) {
                        if (!gameOver) {
                            paused = !paused;
                        }
                    } else if (code == KeyEvent.VK_R) {
                        resetGame();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_LEFT) {
                        leftKeyHeld = false;
                    } else if (code == KeyEvent.VK_RIGHT) {
                        rightKeyHeld = false;
                    } else if (code == KeyEvent.VK_UP) {
                        upKeyHeld = false;
                    } else if (code == KeyEvent.VK_DOWN) {
                        downKeyHeld = false;
                    } else if (code == KeyEvent.VK_SPACE) {
                        spaceKeyHeld = false;
                    }
                }
            });
            
            // 初始化 50 個星點
            stars = new ArrayList<>();
            bullets = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                stars.add(new Star(800, 600));
            }

            // 初始化 1 顆隕石（垂直下落）
            meteor = new Meteor(800);
            
            // 初始化反彈球體
            bouncingMeteor = new BouncingMeteor(800, 600);
            
            // 使用 javax.swing.Timer，每 16ms 更新一次（約 60 FPS）
            gameTimer = new javax.swing.Timer(16, e -> {
                updateGame();
                repaint();
            });
            gameTimer.start();
            // 讓面板取得鍵盤焦點（需先點一下視窗或面板）
            SwingUtilities.invokeLater(this::requestFocusInWindow);
        }

        private void updateGame() {
            if (gameOver || paused) return;
            
            frameCount++;

            // 方向鍵控制紅球在畫面內移動
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            double moveX = 0;
            if (leftKeyHeld) {
                moveX -= MOVE_SPEED;
            }
            if (rightKeyHeld) {
                moveX += MOVE_SPEED;
            }
            circleX += moveX;
            double moveY = 0;
            if (upKeyHeld) {
                moveY -= MOVE_SPEED;
            }
            if (downKeyHeld) {
                moveY += MOVE_SPEED;
            }
            circleY += moveY;
            if (circleX - CIRCLE_RADIUS < 0) {
                circleX = CIRCLE_RADIUS;
            }
            if (panelWidth > 0 && circleX + CIRCLE_RADIUS > panelWidth) {
                circleX = panelWidth - CIRCLE_RADIUS;
            }
            if (panelHeight > 0) {
                if (circleY - CIRCLE_RADIUS < 0) {
                    circleY = CIRCLE_RADIUS;
                }
                if (circleY + CIRCLE_RADIUS > panelHeight) {
                    circleY = panelHeight - CIRCLE_RADIUS;
                }
            }
            
            // 更新所有星點的位置
            for (Star star : stars) {
                star.update(getHeight());
            }

            // 更新隕石位置
            meteor.update(getWidth(), getHeight());
            
            // 更新反彈球體位置
            bouncingMeteor.update(getWidth(), getHeight());

            // 更新子彈位置與命中判定
            updateBullets();
            checkBulletCollision();
            
            // 碰撞偵測：檢查紅球是否與隕石相接觸
            checkCollision();
        }

        private void shootBullet() {
            if (gameOver) {
                return;
            }
            if (bullets.size() >= MAX_BULLETS) {
                return;
            }
            bullets.add(new Bullet(circleX, circleY - CIRCLE_RADIUS));
        }

        private void updateBullets() {
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                bullet.update();
                if (bullet.isOutOfScreen()) {
                    iterator.remove();
                }
            }
        }

        private void checkBulletCollision() {
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();

                // 子彈 vs 垂直隕石
                double meteorCenterX = meteor.x + meteor.size / 2.0;
                double meteorCenterY = meteor.y + meteor.size / 2.0;
                double dx = bullet.x - meteorCenterX;
                double dy = bullet.y - meteorCenterY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                int meteorRadius = meteor.size / 2;
                if (distance <= bullet.radius + meteorRadius) {
                    iterator.remove();
                    meteor.hitAndRespawn(getWidth());
                    score += 10;
                    continue;
                }

                // 子彈 vs 反彈球體
                dx = bullet.x - bouncingMeteor.x;
                dy = bullet.y - bouncingMeteor.y;
                distance = Math.sqrt(dx * dx + dy * dy);
                int bouncingRadius = bouncingMeteor.size / 2;
                if (distance <= bullet.radius + bouncingRadius) {
                    iterator.remove();
                    bouncingMeteor.respawn(getWidth(), getHeight());
                    score += 10;
                }
            }
        }
        
        private void checkCollision() {
            // 計算紅球中心到隕石中心的距離
            double circleCenterX = circleX;
            double circleCenterY = circleY;
            
            // 檢查與垂直隕石的碰撞
            double meteorCenterX = meteor.x + meteor.size / 2.0;
            double meteorCenterY = meteor.y + meteor.size / 2.0;
            double dx = circleCenterX - meteorCenterX;
            double dy = circleCenterY - meteorCenterY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            int meteorRadius = meteor.size / 2;
            if (distance < CIRCLE_RADIUS + meteorRadius) {
                gameOver = true;
                return;
            }
            
            // 檢查與反彈球體的碰撞
            double bouncingCenterX = bouncingMeteor.x;
            double bouncingCenterY = bouncingMeteor.y;
            dx = circleCenterX - bouncingCenterX;
            dy = circleCenterY - bouncingCenterY;
            distance = Math.sqrt(dx * dx + dy * dy);
            int bouncingRadius = bouncingMeteor.size / 2;
            if (distance < CIRCLE_RADIUS + bouncingRadius) {
                gameOver = true;
            }
        }

        private void resetGame() {
            int panelWidth = Math.max(getWidth(), 1);
            int panelHeight = Math.max(getHeight(), 1);

            circleX = panelWidth / 2.0;
            circleY = panelHeight - 80.0;
            frameCount = 0;
            score = 0;
            gameOver = false;
            paused = false;

            leftKeyHeld = false;
            rightKeyHeld = false;
            upKeyHeld = false;
            downKeyHeld = false;
            spaceKeyHeld = false;

            bullets.clear();
            meteor = new Meteor(panelWidth);
            bouncingMeteor = new BouncingMeteor(panelWidth, panelHeight);

            if (!gameTimer.isRunning()) {
                gameTimer.start();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // 啟用抗鋸齒
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 繪製白色星點
            g2d.setColor(Color.WHITE);
            for (Star star : stars) {
                star.draw(g2d);
            }

            // 繪製隕石
            meteor.draw(g2d);

            // 繪製反彈球體
            bouncingMeteor.draw(g2d);

            // 繪製子彈
            for (Bullet bullet : bullets) {
                bullet.draw(g2d);
            }

            // 左上角顯示 frame count，確認遊戲持續更新
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2d.drawString("Frame: " + frameCount, 16, 28);
            g2d.drawString("Score: " + score, 16, 54);
            String statusText = gameOver ? "Status: GAME OVER"
                    : (paused ? "Status: PAUSED" : "Status: RUNNING");
            g2d.drawString(statusText, 16, 80);
            g2d.drawString("P: Pause  R: Restart", 16, 106);
            
            // 繪製紅球
            g2d.setColor(Color.RED);
            int drawX = (int)(circleX - CIRCLE_RADIUS);
            int drawY = (int)(circleY - CIRCLE_RADIUS);
            g2d.fillOval(drawX, drawY, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
            
            // 遊戲結束訊息
            if (gameOver) {
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                String gameOverText = "GAME OVER";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(gameOverText)) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(gameOverText, textX, textY);
                
                g2d.setFont(new Font("Arial", Font.PLAIN, 24));
                String finalScoreText = "Score: " + score;
                fm = g2d.getFontMetrics();
                textX = (getWidth() - fm.stringWidth(finalScoreText)) / 2;
                g2d.drawString(finalScoreText, textX, textY + 50);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) { }

        @Override
        public void mousePressed(MouseEvent e) {
            requestFocusInWindow();
        }

        @Override
        public void mouseReleased(MouseEvent e) { }

        @Override
        public void mouseEntered(MouseEvent e) { }

        @Override
        public void mouseExited(MouseEvent e) { }

    }

    public static void main(String[] args) {
        // 在事件調度執行緒中建立 GUI
        SwingUtilities.invokeLater(() -> new CircleFollowsMouse());
    }
}
