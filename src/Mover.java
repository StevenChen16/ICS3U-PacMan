import javax.swing.JLabel;

@SuppressWarnings("serial")

/*
* This class ...
*/
public class Mover extends JLabel{

    //
    private int row;
    private int column;

    //
    private int dRow;
    private int dColumn;

    //
    private boolean isDead;

    //
    public Mover(int row, int column) {
        super();
        this.row = row;
        this.column = column;
    }

    //
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getdRow() {
        return dRow;
    }

    public void setdRow(int dRow) {
        this.dRow = dRow;
    }

    public int getdColumn() {
        return dColumn;
    }

    public void setdColumn(int dColumn) {
        this.dColumn = dColumn;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    // This method ...
    public void move() {
        row += dRow;
        column += dColumn;
    }

    // This method ...
    public void setDirection(int direction) {

        //
        dRow = 0;
        dColumn = 0;

        //
        if (direction == 0)
            dColumn = -1;
        else if (direction == 1)
            dRow = -1;
        else if (direction == 2)
            dColumn = 1;
        else if (direction == 3)
            dRow = 1;
    }

    // This method ...
    public int getDirection() {

        if (dRow == 0 && dColumn == -1)
            return 0;
        else if (dRow == -1 && dColumn == 0)
            return 1;
        else if (dRow == 0 && dColumn == 1)
            return 2;
        else
            return 3;
    }

    // This method ...
    public int getNextRow() {

        return row + dRow;
    }

    // This method ...
    public int getNextColumn() {

        return column + dColumn;
    }
}