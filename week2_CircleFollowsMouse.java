import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class week2_CircleFollowsMouse extends JFrame {
    private CirclePanel circlePanel;

    public week2_CircleFollowsMouse() {
        // 設定視窗標題
        setTitle("Circle Follows Mouse");
        
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

    // 隕石類別
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

        public void draw(Graphics2D g2d) {
            g2d.setColor(Color.GRAY);
            g2d.fillOval((int) x, (int) y, size, size);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval((int) x + size / 4, (int) y + size / 4, size / 3, size / 3);
        }
    }

    // 內部類別：繪製圓形的面板
    class CirclePanel extends JPanel implements MouseMotionListener, MouseListener {
        // 紅球當前位置（使用 double 以支援平滑移動）
        private double circleX = 400;
        private double circleY = 300;
        
        // 滑鼠目標位置
        private int targetX = 400;
        private int targetY = 300;
        
        private final int CIRCLE_RADIUS = 20;
        private final double EASING_FACTOR = 0.1; // 緩動係數（0-1），越小越慢
        
        // Boost 狀態
        private boolean isBoosting = false;
        
        // 星點列表
        private ArrayList<Star> stars;
        private Meteor meteor;
        private long frameCount = 0;
        private javax.swing.Timer gameTimer;

        public CirclePanel() {
            // 設定黑色背景
            setBackground(Color.BLACK);
            
            // 加入滑鼠移動監聽器
            addMouseMotionListener(this);
            
            // 加入滑鼠點擊監聽器
            addMouseListener(this);
            
            // 初始化 50 個星點
            stars = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                stars.add(new Star(800, 600));
            }

            // 初始化 1 顆隕石
            meteor = new Meteor(800);
            
            // 使用 javax.swing.Timer，每 16ms 更新一次（約 60 FPS）
            gameTimer = new javax.swing.Timer(16, e -> {
                updateGame();
                repaint();
            });
            gameTimer.start();
        }

        private void updateGame() {
            frameCount++;

            // 更新紅球位置，使用緩動公式實現慣性跟隨
            double dx = targetX - circleX;
            double dy = targetY - circleY;
            
            circleX += dx * EASING_FACTOR;
            circleY += dy * EASING_FACTOR;
            
            // 更新所有星點的位置
            for (Star star : stars) {
                star.update(getHeight());
            }

            // 更新隕石位置
            meteor.update(getWidth(), getHeight());
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

            // 左上角顯示 frame count，確認遊戲持續更新
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2d.drawString("Frame: " + frameCount, 16, 28);
            
            // 繪製圓形（根據 boost 狀態調整顏色和大小）
            if (isBoosting) {
                g2d.setColor(Color.YELLOW);
                int boostedRadius = CIRCLE_RADIUS * 2;
                int drawX = (int)(circleX - boostedRadius);
                int drawY = (int)(circleY - boostedRadius);
                g2d.fillOval(drawX, drawY, boostedRadius * 2, boostedRadius * 2);
            } else {
                g2d.setColor(Color.RED);
                int drawX = (int)(circleX - CIRCLE_RADIUS);
                int drawY = (int)(circleY - CIRCLE_RADIUS);
                g2d.fillOval(drawX, drawY, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // 更新目標位置
            targetX = e.getX();
            targetY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            // 也處理拖曳事件
            targetX = e.getX();
            targetY = e.getY();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // 點擊時觸發 boost 效果
            if (!isBoosting) {
                isBoosting = true;
                System.out.println("Engine Boost!");
                
                // 0.2 秒後還原
                javax.swing.Timer boostTimer = new javax.swing.Timer(200, event -> {
                    isBoosting = false;
                    repaint();
                });
                boostTimer.setRepeats(false);
                boostTimer.start();
                
                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }

    public static void main(String[] args) {
        // 在事件調度執行緒中建立 GUI
        SwingUtilities.invokeLater(() -> new week2_CircleFollowsMouse());
    }
}
