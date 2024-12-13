import javax.swing.JLabel;

@SuppressWarnings("serial")

/**
 * This class represents a movable entity in the game (Pac-Man or Ghost).
 * It handles position tracking, movement direction, and state management.
 * 负责处理位置跟踪、移动方向和状态管理。
 */
public class Mover extends JLabel {

    // Position coordinates 位置坐标
    private int row;        // Current row 当前行
    private int column;     // Current column 当前列

    // Movement direction 移动方向
    private int dRow;       // Row direction (-1: up, 1: down) 行方向（-1：上，1：下）
    private int dColumn;    // Column direction (-1: left, 1: right) 列方向（-1：左，1：右）

    // State flags 状态标记
    private boolean isDead;     // Indicates if entity is dead 表示实体是否死亡
    private boolean isEaten;    // Indicates if ghost is eaten 表示幽灵是否被吃
    private int respawnTimer;   // Timer for ghost respawn 幽灵重生计时器

    /**
     * Creates a new Mover at the specified position.
     * 在指定位置创建新的移动实体。
     * 
     * @param row Initial row position 初始行位置
     * @param column Initial column position 初始列位置
     */
    public Mover(int row, int column) {
        super();
        this.row = row;
        this.column = column;
    }

    // Position getters and setters 位置的获取和设置方法
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public int getColumn() { return column; }
    public void setColumn(int column) { this.column = column; }

    // Direction getters and setters 方向的获取和设置方法
    public int getdRow() { return dRow; }
    public void setdRow(int dRow) { this.dRow = dRow; }
    public int getdColumn() { return dColumn; }
    public void setdColumn(int dColumn) { this.dColumn = dColumn; }

    // State getters and setters 状态的获取和设置方法
    public boolean isDead() { return isDead; }
    public void setDead(boolean isDead) { this.isDead = isDead; }
    public boolean isEaten() { return isEaten; }
    public void setEaten(boolean eaten) { this.isEaten = eaten; }

    /**
     * Updates position based on current direction.
     */
    public void move() {
        row += dRow;
        column += dColumn;
    }

    /**
     * Sets movement direction based on input.
     * 根据输入设置移动方向。
     * 
     * @param direction Direction code (0: left, 1: up, 2: right, 3: down)
     */
    public void setDirection(int direction) {
        // Reset direction 重置方向
        dRow = 0;
        dColumn = 0;

        // Set new direction 设置新方向
        if (direction == 0)
            dColumn = -1;        // Left
        else if (direction == 1)
            dRow = -1;          // Up
        else if (direction == 2)
            dColumn = 1;         // Right
        else if (direction == 3)
            dRow = 1;           // Down
    }

    /**
     * Gets current movement direction.
     * 获取当前移动方向。
     * 
     * @return Direction code
     *         方向代码（0：左，1：上，2：右，3：下）
     */
    public int getDirection() {
        if (dRow == 0 && dColumn == -1)
            return 0;            // Left 左
        else if (dRow == -1 && dColumn == 0)
            return 1;            // Up 上
        else if (dRow == 0 && dColumn == 1)
            return 2;            // Right 右
        else
            return 3;            // Down 下
    }

    /**
     * Calculates the next row position based on current direction.
     * 
     * @return Next row position 下一行位置
     */
    public int getNextRow() {
        return row + dRow;
    }

    /**
     * Calculates the next column position based on current direction.
     * 基于当前方向计算下一列位置。
     * 
     * @return Next column position 下一列位置
     */
    public int getNextColumn() {
        return column + dColumn;
    }
}