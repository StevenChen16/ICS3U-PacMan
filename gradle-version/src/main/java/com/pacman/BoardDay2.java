//BoardDay2.java
package com.pacman;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class BoardDay2 extends BoardDay1 {
    
    public BoardDay2() {
        super();
    }

    @Override
    protected void performMove() {
        if (pacman != null && !pacman.isDead()) {
            int nextRow = pacman.getNextRow();
            int nextCol = pacman.getNextColumn();
            int currentCol = pacman.getColumn();

            // 处理边界传送
            if (nextCol < 0) {
                // 完全清除当前位置
                Cell currentCell = mazeArray[pacman.getRow()][currentCol];
                currentCell.setIcon(Icons.BLANK);
                currentCell.setItem(' ');
                
                // 设置新位置
                nextCol = 26;
                pacman.setColumn(26);
                Cell nextCell = mazeArray[pacman.getRow()][nextCol];
                nextCell.setIcon(Icons.PACMAN[pacman.getDirection()]);
                nextCell.setItem('P');
                pacman.move();
                return;
            } else if (nextCol > 26) {
                // 完全清除当前位置
                Cell currentCell = mazeArray[pacman.getRow()][currentCol];
                currentCell.setIcon(Icons.BLANK);
                currentCell.setItem(' ');
                
                // 设置新位置
                nextCol = 0;
                pacman.setColumn(0);
                Cell nextCell = mazeArray[pacman.getRow()][nextCol];
                nextCell.setIcon(Icons.PACMAN[pacman.getDirection()]);
                nextCell.setItem('P');
                pacman.move();
                return;
            }

            // 正常移动处理
            if (nextRow >= 0 && nextRow < 25 && isValidMove(nextRow, nextCol)) {
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
    }
}