import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Circle - Keyboard Control");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new GamePanel());
        setVisible(true);
    }
}
