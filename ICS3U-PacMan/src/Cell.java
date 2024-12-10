import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Cell extends JLabel {
    // Character representation of cell contents:
    // 'P' - Pac-Man
    // '0','1','2' - Different ghost types
    // 'W' - Wall
    // 'F' - Food pellet
    // 'D' - Ghost house door
    // ' ' - Empty space
    private char item;

    public Cell(char item) {
        super();
        this.item = item;
        setCodeIcon();
    }

    public char getItem() {
        return item;
    }

    public void setItem(char item) {
        this.item = item;
    }

    /**
     * Sets the appropriate icon based on the cell's item code.
     * Maps each character code to its corresponding sprite image.
     */
    private void setCodeIcon() {
        if (item == 'P')
            setIcon(Icons.PACMAN[0]);
        else if (item == '0')
            setIcon(Icons.GHOST[0]);
        else if (item == '1')
            setIcon(Icons.GHOST[1]);
        else if (item == '2')
            setIcon(Icons.GHOST[2]);
        else if (item == 'W')
            setIcon(Icons.WALL);
        else if (item == 'F')
            setIcon(Icons.FOOD);
        else if (item == 'D')
            setIcon(Icons.DOOR);
    }
}