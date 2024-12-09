import javax.swing.*;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;

@SuppressWarnings("serial")
public class PacManGUI extends JFrame {
    private BoardDay3 board;

    public PacManGUI() {
        // 初始化游戏面板
        board = new BoardDay3();
        
        // 设置窗口属性
        setSize(600, 650);  // 增加高度以容纳分数面板
        setTitle("Yucheng Chen: PacMan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 添加图标
        try {
            Image icon = ImageIO.read(new File("../resources/images/icon.jpg"));
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Error loading icon: " + e.getMessage());
        }

        // 添加游戏面板到窗口
        add(board);

        // 设置窗口可见性
        setVisible(true);

        // 确保游戏面板能接收键盘输入
        board.requestFocusInWindow();
        board.setFocusable(true);
        board.addKeyListener(board);
    }
}