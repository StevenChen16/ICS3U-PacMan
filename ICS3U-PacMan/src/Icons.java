import javax.swing.ImageIcon;

public class Icons {
    // Image directory path - can be changed to configure resource location
    public static final String IMAGE_PATH = "../resources/images/";
    
    // Game icons
    public static final ImageIcon WALL = new ImageIcon(IMAGE_PATH + "Wall.bmp");
    public static final ImageIcon FOOD = new ImageIcon(IMAGE_PATH + "Food.bmp");
    public static final ImageIcon BLANK = new ImageIcon(IMAGE_PATH + "Black.bmp");
    public static final ImageIcon DOOR = new ImageIcon(IMAGE_PATH + "Black.bmp");
    public static final ImageIcon SKULL = new ImageIcon(IMAGE_PATH + "Skull.bmp");
    public static final ImageIcon Cherry = new ImageIcon(IMAGE_PATH + "Cherry.bmp");

    // Pacman animation frames
    public static final ImageIcon[] PACMAN = {
        new ImageIcon(IMAGE_PATH + "PacMan0.gif"),
        new ImageIcon(IMAGE_PATH + "PacMan1.gif"),
        new ImageIcon(IMAGE_PATH + "PacMan2.gif"),
        new ImageIcon(IMAGE_PATH + "PacMan3.gif"),
    };

    // Ghost animation frames
    public static final ImageIcon[] GHOST = {
        new ImageIcon(IMAGE_PATH + "Ghost0.bmp"),
        new ImageIcon(IMAGE_PATH + "Ghost1.bmp"),
        new ImageIcon(IMAGE_PATH + "Ghost2.bmp"),
    };
}