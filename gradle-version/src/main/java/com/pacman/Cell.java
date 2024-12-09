package com.pacman;

import javax.swing.JLabel;

@SuppressWarnings("serial")

/*
* This class ...
*/
public class Cell extends JLabel{

    //
    private char item;

    // This method ...
    public Cell(char item) {

        super();
        this.item = item;

        //
        setCodeIcon();
    }

    //
    public char getItem() {
        return item;
    }

    public void setItem(char item) {
        this.item = item;
    }

    // This method ...
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