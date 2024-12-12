// Node.java
package com.pacman;

import java.util.Objects;

public class Node {
    int row, col;
    int distance = Integer.MAX_VALUE;
    Node previous = null;
    boolean visited = false;
    
    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return this.row == other.row && this.col == other.col;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}