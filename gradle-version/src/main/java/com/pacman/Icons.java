package com.pacman;

import javax.swing.ImageIcon;

public class Icons {
    // 使用类加载器加载资源
    private static ImageIcon loadIcon(String name) {
        java.net.URL url = Icons.class.getClassLoader().getResource("images/" + name);
        if (url == null) {
            System.err.println("Could not load image: " + name);
            return new ImageIcon();
        }
        return new ImageIcon(url);
    }
    
    // Game icons
    public static final ImageIcon WALL = loadIcon("Wall.bmp");
    public static final ImageIcon FOOD = loadIcon("Food.bmp");
    public static final ImageIcon BLANK = loadIcon("Black.bmp");
    public static final ImageIcon DOOR = loadIcon("Black.bmp");
    public static final ImageIcon SKULL = loadIcon("Skull.bmp");

    // Pacman animation frames
    public static final ImageIcon[] PACMAN = {
        loadIcon("PacMan0.gif"),
        loadIcon("PacMan1.gif"),
        loadIcon("PacMan2.gif"),
        loadIcon("PacMan3.gif"),
    };

    // Ghost animation frames
    public static final ImageIcon[] GHOST = {
        loadIcon("Ghost0.bmp"),
        loadIcon("Ghost1.bmp"),
        loadIcon("Ghost2.bmp"),
    };
}