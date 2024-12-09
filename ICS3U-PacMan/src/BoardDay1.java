import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class BoardDay1 extends JPanel implements KeyListener, ActionListener {
    protected Cell[][] mazeArray = new Cell[25][27];
    protected Mover pacman;
    protected Timer gameTimer;
    protected int score = 0;
    protected int pellets = 0;

    public BoardDay1() {
        setLayout(new GridLayout(25, 27));
        setBackground(Color.BLACK);
        loadBoard();
        
        gameTimer = new Timer(250, this);
        
        setFocusable(true);
        requestFocusInWindow();
    }

    protected void loadBoard() {
        int row = 0;
        Scanner inputFile;

        try {
            inputFile = new Scanner(new File("../resources/maze.txt"));

            while (inputFile.hasNext()) {
                char[] lineArray = inputFile.nextLine().toCharArray();

                for (int column = 0; column < lineArray.length; column++) {
                    mazeArray[row][column] = new Cell(lineArray[column]);
                    
                    if (lineArray[column] == 'F') {
                        pellets++;
                    }
                    
                    if (lineArray[column] == 'P') {
                        pacman = new Mover(row, column);
                    }

                    add(mazeArray[row][column]);
                }
                row++;
            }

            inputFile.close();

        } catch (FileNotFoundException error) {
            System.out.println("maze.txt not found");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (pacman != null && !pacman.isDead()) {
            performMove();
        }
    }

    protected void performMove() {
        int nextRow = pacman.getNextRow();
        int nextCol = pacman.getNextColumn();

        if (isValidMove(nextRow, nextCol)) {
            Cell currentCell = mazeArray[pacman.getRow()][pacman.getColumn()];
            Cell nextCell = mazeArray[nextRow][nextCol];
            
            currentCell.setIcon(Icons.BLANK);
            currentCell.setItem(' ');
            
            if (nextCell.getItem() == 'F') {
                score += 10;
                pellets--;
            }
            
            pacman.move();
            nextCell.setIcon(Icons.PACMAN[pacman.getDirection()]);
            nextCell.setItem('P');
        }
    }

    protected boolean isValidMove(int row, int col) {
        return row >= 0 && row < 25 && col >= 0 && col < 27 && 
               mazeArray[row][col].getItem() != 'W';
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }

        int key = e.getKeyCode();
        if (key >= KeyEvent.VK_LEFT && key <= KeyEvent.VK_DOWN) {
            pacman.setDirection(key - 37);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}