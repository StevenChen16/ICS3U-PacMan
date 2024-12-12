package com.pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;

@SuppressWarnings("serial")
public class MenuGUI extends JFrame {
    // Declare main components 声明主要组件
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton startButton;
    private JButton instructionsButton;
    private JButton exitButton;

    public MenuGUI() {
        // Initialize the frame 初始化框架
        setupFrame();
        
        // Create and setup components 创建并设置组件
        createComponents();
        
        // Add components to panel 将组件添加到面板
        layoutComponents();
        
        // Make frame visible 使框架可见
        setVisible(true);
    }

    private void setupFrame() {
        // Set basic frame properties 设置基本框架属性
        setTitle("Pac-Man Game Menu");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Try to set icon 尝试设置图标
        try {
            Image icon = ImageIO.read(new File("../resources/images/icon.jpg"));
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Error loading icon: " + e.getMessage());
        }
    }

    private void createComponents() {
        // Initialize main panel 初始化主面板
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.BLACK);

        // Create title 创建标题
        titleLabel = new JLabel("PAC-MAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create buttons 创建按钮
        startButton = createStyledButton("Start Game");
        instructionsButton = createStyledButton("Instructions");
        exitButton = createStyledButton("Exit");

        // Add button actions 添加按钮动作
        startButton.addActionListener(e -> startGame());
        instructionsButton.addActionListener(e -> showInstructions());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private JButton createStyledButton(String text) {
        // Create styled button 创建样式化按钮
        JButton button = new JButton(text);
        button.setFont(new Font("SimHEI", Font.BOLD, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 50));
        button.setBackground(Color.YELLOW);
        button.setFocusPainted(false);
        
        // Add hover effect 添加悬停效果
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.ORANGE);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.YELLOW);
            }
        });
        
        return button;
    }

    private void layoutComponents() {
        // Add empty space at top 在顶部添加空白
        mainPanel.add(Box.createVerticalStrut(50));
        
        // Add title 添加标题
        mainPanel.add(titleLabel);
        
        // Add space between title and buttons 在标题和按钮之间添加空间
        mainPanel.add(Box.createVerticalStrut(50));
        
        // Add buttons with spacing 添加按钮并设置间距
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(instructionsButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(exitButton);
        
        // Add panel to frame 将面板添加到框架
        add(mainPanel);
    }

    private void startGame() {
        // Start the game 开始游戏
        new PacManGUI();
        // Hide menu window 隐藏菜单窗口
        this.dispose();
    }

    private void showInstructions() {
        // Create instructions text 创建说明文本
        String instructions = 
            "Game Instructions 游戏说明:\n\n" +
            "1. Movement 移动:\n" +
            "   - Use arrow keys to move Pac-Man\n" +
            "   - 使用方向键移动吃豆人\n\n" +
            "2. Objectives 目标:\n" +
            "   - Eat all dots to win\n" +
            "   - 吃掉所有豆子来获得胜利\n" +
            "   - Avoid ghosts unless powered up\n" +
            "   - 避开幽灵，除非处于能量状态\n\n" +
            "3. Power Pellets 能量豆:\n" +
            "   - Large dots that let you eat ghosts\n" +
            "   - 大豆子可以让你吃掉幽灵\n\n" +
            "4. Controls 控制:\n" +
            "   - P: Pause game 暂停游戏\n" +
            "   - R: Restart game 重新开始\n" +
            "   - M: Mute sound 静音\n";

        // Show instructions in dialog 在对话框中显示说明
        JTextArea textArea = new JTextArea(instructions);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Instructions 游戏说明", JOptionPane.INFORMATION_MESSAGE);
    }
}