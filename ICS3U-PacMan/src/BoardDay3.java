import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import javax.sound.sampled.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;

@SuppressWarnings("serial")
public class BoardDay3 extends BoardDay2 {
    // Constants
    private static final int POWER_DURATION = 300;
    private static final int CHERRY_SCORE = 100;
    private static final int GHOST_SCORE = 200;
    
    // Game elements
    protected Mover[] ghosts;
    protected Random random;
    protected Timer ghostTimer;
    protected Timer cherryTimer;
    protected Point cherryPosition;
    
    // Game state
    protected int lives;
    protected boolean isPaused;
    protected boolean isPowered;
    protected int powerTimer;
    protected int ghostsEaten;
    
    // UI elements
    protected JLabel scoreLabel;
    protected JLabel livesLabel;
    protected JLabel messageLabel;
    
    // Sound system
    protected Clip beginningSound;
    protected Clip chompSound;
    protected Clip fruitSound;
    protected Clip ghostSound;
    protected Clip deathSound;
    protected Clip powerSound;
    protected Clip intermSound;

    protected JLabel difficultyLabel;
    protected JLabel timeLabel;
    protected JLabel ghostStatsLabel;
    protected JPanel mazePanel;
    
    private static final int MAX_HIGH_SCORES = 10;
    private static final int[] DIFFICULTY_SPEEDS = {250, 200, 150}; // Easy, Normal, Hard
    
    protected int difficulty = 1; // 0=Easy, 1=Normal, 2=Hard
    protected long gameStartTime;
    protected int[] ghostEatenCounts = new int[3];
    protected boolean isPowerUpActive = false;
    protected float gameSpeed = 1.0f;
    protected boolean isMuted = false;
    protected List<HighScore> highScores = new ArrayList<>();

    private static final char POWER_PELLET = 'P';  // Power pellet
    private static final int[][] POWER_PELLET_POSITIONS = {
        {3, 1},
        {3, 25},
        {23, 1},
        {23, 25}, 
    };

    // for dijkstra's algorithm
    private Map<Point, List<Point>> pathCache = new HashMap<>();
    private static final int PATH_UPDATE_INTERVAL = 10;
    private int pathUpdateCounter = 0;


    private static class HighScore implements Comparable<HighScore> {
        String name;
        int score;
        
        public HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }
        
        @Override
        public int compareTo(HighScore other) {
            return other.score - this.score; // descending order
        }
    }
    
    public BoardDay3() {
        super();
        initializeGame();
        setupGhosts();
        setupCherryTimer();
        initializePowerPellets();
        setupUI();
        
        spawnCherry();
        
        gameStartTime = System.currentTimeMillis();
        loadHighScores();
        updateDifficultyParams();
        
        loadSounds();
        playSound(beginningSound);
    }
    
    private void initializeGame() {
        lives = 3;
        isPaused = false;
        isPowered = false;
        powerTimer = 0;
        ghostsEaten = 0;
        random = new Random();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        mazePanel = new JPanel(new GridLayout(25, 27));
        mazePanel.setBackground(Color.BLACK);
        
        difficultyLabel = new JLabel("Normal");
        timeLabel = new JLabel("Time: 0:00");
        ghostStatsLabel = new JLabel(String.format("Ghosts: 0/%d", ghosts != null ? ghosts.length : 3));
        
        // Move components
        Component[] components = getComponents();
        for (Component comp : components) {
            remove(comp);
            mazePanel.add(comp);
        }
        
        // Create score panel
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        scorePanel.setBackground(Color.BLACK);
        
        // Initialize labels
        scoreLabel = new JLabel("Score: 0");
        livesLabel = new JLabel("Lives: " + lives);
        messageLabel = new JLabel("Ready!");
        
        scoreLabel.setForeground(Color.WHITE);
        livesLabel.setForeground(Color.WHITE);
        messageLabel.setForeground(Color.YELLOW);
        
        scorePanel.add(scoreLabel);
        scorePanel.add(livesLabel);
        scorePanel.add(messageLabel);
        
        add(scorePanel, BorderLayout.NORTH);
        add(mazePanel, BorderLayout.CENTER);
        validate();

        JPanel statusPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statusPanel.setBackground(Color.BLACK);
        
        // left info group
        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftInfo.setBackground(Color.BLACK);
        scoreLabel = new JLabel("Score: 0");
        difficultyLabel = new JLabel("Normal");
        scoreLabel.setForeground(Color.WHITE);
        difficultyLabel.setForeground(Color.YELLOW);
        leftInfo.add(scoreLabel);
        leftInfo.add(difficultyLabel);
        
        // middle info group
        JPanel centerInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        centerInfo.setBackground(Color.BLACK);
        livesLabel = new JLabel("Lives: 3");
        timeLabel = new JLabel("Time: 0:00");
        livesLabel.setForeground(Color.WHITE);
        timeLabel.setForeground(Color.WHITE);
        centerInfo.add(livesLabel);
        centerInfo.add(timeLabel);
        
        // right info group
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightInfo.setBackground(Color.BLACK);
        ghostStatsLabel = new JLabel("Ghosts: 0");
        messageLabel = new JLabel("Ready!");
        ghostStatsLabel.setForeground(Color.WHITE);
        messageLabel.setForeground(Color.YELLOW);
        rightInfo.add(ghostStatsLabel);
        rightInfo.add(messageLabel);
        
        statusPanel.add(leftInfo);
        statusPanel.add(centerInfo);
        statusPanel.add(rightInfo);
        
        add(statusPanel, BorderLayout.NORTH);

        Timer statsTimer = new Timer(100, e -> {
            updateGhostStats();
            updateTimeLabel();
        });
        statsTimer.start();
    }

    private void updateGhostStats() {
        if (ghostStatsLabel != null && ghosts != null) {
            int totalGhosts = ghosts.length;
            ghostStatsLabel.setText(String.format("Ghosts: %d/%d", ghostsEaten, totalGhosts));
        }
    }
    
    private void resetGhost(Mover ghost) {
        // clear current position
        mazeArray[ghost.getRow()][ghost.getColumn()].setIcon(Icons.BLANK);
        mazeArray[ghost.getRow()][ghost.getColumn()].setItem(' ');
        
        // reset position
        ghost.setRow(11);
        ghost.setColumn(13);
        
        // display ghost at new position
        Cell cell = mazeArray[11][13];
        cell.setIcon(ghost.getIcon());
        cell.setItem('G');
    }

    private void updateDifficultyParams() {
        // Update difficulty label
        if (ghostTimer != null) {
            ghostTimer.setDelay(DIFFICULTY_SPEEDS[difficulty]);
        }
        if (cherryTimer != null) {
            cherryTimer.setDelay(10000 - (difficulty * 2000));
        }
    }

    private void loadHighScores() {
        try {
            File file = new File("pacman_scores.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    highScores.add(new HighScore(parts[0], Integer.parseInt(parts[1])));
                }
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
        }
    }

    private void saveHighScore() {
        try {
            // 
            highScores.add(new HighScore("Player", score));
            // 
            Collections.sort(highScores);
            if (highScores.size() > MAX_HIGH_SCORES) {
                highScores = highScores.subList(0, MAX_HIGH_SCORES);
            }
            
            // 保存到文件
            BufferedWriter writer = new BufferedWriter(new FileWriter("pacman_scores.txt"));
            for (HighScore hs : highScores) {
                writer.write(hs.name + "," + hs.score + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }

    // Load sound files
    private void updateTimeLabel() {
        long currentTime = System.currentTimeMillis() - gameStartTime;
        long seconds = currentTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        timeLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
    }

    private void handleSpecialEvents() {
        if (random.nextDouble() < 0.01) { // 1% probability of special event
            int eventType = random.nextInt(4);
            switch (eventType) {
                case 0: // Ghost Speed Up
                    ghostTimer.setDelay((int)(ghostTimer.getDelay() * 0.8));
                    messageLabel.setText("Ghosts Speed Up!");
                    Timer resetSpeedTimer = new Timer(5000, e -> {
                        ghostTimer.setDelay(DIFFICULTY_SPEEDS[difficulty]);
                        ((Timer)e.getSource()).stop();
                    });
                    resetSpeedTimer.setRepeats(false);
                    resetSpeedTimer.start();
                    break;
                    
                case 1: // Double Score
                    score *= 2;
                    messageLabel.setText("Double Score!");
                    break;
                    
                case 2: // PacMan invincible
                    isPowerUpActive = true;
                    messageLabel.setText("Invisible PacMan!");
                    Timer invisibleTimer = new Timer(5000, e -> {
                        isPowerUpActive = false;
                        ((Timer)e.getSource()).stop();
                    });
                    invisibleTimer.setRepeats(false);
                    invisibleTimer.start();
                    break;
            }
        }
    }
    
    private void resetGame() {
        // reset game state
        score = 0;
        lives = 3;
        isPaused = false;
        isPowered = false;
        powerTimer = 0;
        ghostsEaten = 0;
        
        // reset ghost stats
        gameStartTime = System.currentTimeMillis();
        for (int i = 0; i < ghostEatenCounts.length; i++) {
            ghostEatenCounts[i] = 0;
        }
        
        // reset game elements
        resetPositions();
        
        // reset UI elements
        scoreLabel.setText("Score: 0");
        livesLabel.setText("Lives: " + lives);
        messageLabel.setText("Ready!");
        
        // start game
        if (!gameTimer.isRunning()) {
            gameTimer.start();
            ghostTimer.start();
            cherryTimer.start();
        }
    }
    


    private void setupGhosts() {
        ghosts = new Mover[3];
        
        int[][] ghostPositions = {
            {11, 13},  // Blinky
            {11, 11},  // Pinky
            {11, 15}   // Inky
        };
        
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i] = new Mover(ghostPositions[i][0], ghostPositions[i][1]);
            ghosts[i].setIcon(Icons.GHOST[i]);
            
            // Add verification
            Cell cell = mazeArray[ghostPositions[i][0]][ghostPositions[i][1]];
            if (cell == null) {
                System.err.println("Error: Maze cell is null at position " + ghostPositions[i][0] + "," + ghostPositions[i][1]);
                continue;
            }
            
            cell.setIcon(ghosts[i].getIcon());
            cell.setItem('G');
        }
        
        // Add verification output
        // System.out.println("Ghosts initialized: " + ghosts.length); //Debugging
        
        ghostTimer = new Timer(200, e -> {
            if (!isPaused && !pacman.isDead()) {
                moveGhosts();
                updatePowerState();
            }
        });
        ghostTimer.start();
    }
    
    private void setupCherryTimer() {
        cherryTimer = new Timer(10000, e -> {
            if (!isPaused && !pacman.isDead()) {
                spawnCherry();
            }
        });
        cherryTimer.start();
    }
    
    private Clip loadClip(String path) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path));
        clip.open(inputStream);
        return clip;
    }
    
    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    private void spawnCherry() {
        // Find a valid position for cherry
        int attempts = 0;
        while (attempts < 50) {
            int row = random.nextInt(25);
            int col = random.nextInt(27);
            
            if (mazeArray[row][col].getItem() == ' ') {
                mazeArray[row][col].setIcon(new ImageIcon("images/Cherry.bmp"));
                mazeArray[row][col].setItem('C');
                cherryPosition = new Point(row, col);
                break;
            }
            attempts++;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused && !pacman.isDead()) {
            // 基本移动
            super.actionPerformed(e);
            
            // 游戏逻辑检查
            checkGhostCollisions();  // 添加碰撞检测
            handleSpecialEvents();   // 添加特殊事件处理
            
            // 更新显示
            updateScore();
            updateTimeLabel();
            updateGhostStats();
            
            // 更新Power模式状态
            if (isPowered) {
                messageLabel.setText(String.format("Power Mode! (%d)", powerTimer));
            }
        }
    }
    
    private void moveGhosts() {
        for (int i = 0; i < ghosts.length; i++) {
            Mover ghost = ghosts[i];
            if (!ghost.isEaten()) {  // 只移动未被吃掉的幽灵
                if (isPowered) {
                    moveGhostScared(ghost);
                } else {
                    moveGhostNormal(ghost, i);  // 传入幽灵的索引作为类型
                }
                moveGhost(ghost);
            }
        }
    }
    
    private void moveGhostScared(Mover ghost) {
        if (random.nextDouble() > 0.3) {
            // 逃离Pacman - 方向要反过来
            int dx = pacman.getColumn() - ghost.getColumn();
            int dy = pacman.getRow() - ghost.getRow();
            
            if (Math.abs(dx) > Math.abs(dy)) {
                ghost.setDirection(dx > 0 ? 0 : 2);  // 如果PacMan在右边，往左走(0)；在左边，往右走(2)
            } else {
                ghost.setDirection(dy > 0 ? 1 : 3);  // 如果PacMan在下边，往上走(1)；在上边，往下走(3)
            }
        } else {
            ghost.setDirection(random.nextInt(4));  // 30%概率随机移动
        }
    }
    
    private void moveGhostNormal(Mover ghost, int ghostType) {
        if (random.nextDouble() > 0.3) {  // 70% probability of moving
            switch (ghostType) {
                case 0: // Blinky - Derectly Chase
                    // chaseDirectly(ghost);
                    moveGhostWithDijkstra(ghost);
                    break;
                case 1: // Pinky
                    predictAndChase(ghost);
                    break;
                case 2: // Inky
                    chaseWithRandomness(ghost);
                    break;
            }
        } else {
            ghost.setDirection(random.nextInt(4));  // 30% probability of random move
        }
    }
    
    private void chaseDirectly(Mover ghost) {
        // Determine direction based on PacMan's position
        int dx = pacman.getColumn() - ghost.getColumn();
        int dy = pacman.getRow() - ghost.getRow();
        
        if (Math.abs(dx) > Math.abs(dy)) {
            ghost.setDirection(dx > 0 ? 2 : 0);  // if PacMan is on the right, move right(2); on the left, move left(0)
        } else {
            ghost.setDirection(dy > 0 ? 3 : 1);  // if PacMan is below, move down(3); above, move up(1)
        }
    }
    
    private void predictAndChase(Mover ghost) {
        // predict PacMan's position in 4 steps
        int predictedX = pacman.getColumn() + (pacman.getdColumn() * 4);
        int predictedY = pacman.getRow() + (pacman.getdRow() * 4);
        
        int dx = predictedX - ghost.getColumn();
        int dy = predictedY - ghost.getRow();
        
        if (Math.abs(dx) > Math.abs(dy)) {
            ghost.setDirection(dx > 0 ? 2 : 0);
        } else {
            ghost.setDirection(dy > 0 ? 3 : 1);
        }
    }
    
    private void chaseWithRandomness(Mover ghost) {
        if (random.nextDouble() > 0.5) {  // 50% probability of chasing directly
            chaseDirectly(ghost);
        } else {  // 50% probability of random move
            ghost.setDirection(random.nextInt(4));
        }
    }

    private List<Point> findPathDijkstra(int startRow, int startCol, int targetRow, int targetCol) {
        // create graph
        Node[][] graph = new Node[25][27];
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 27; j++) {
                if (isValidMove(i, j)) {
                    graph[i][j] = new Node(i, j);
                }
            }
        }
        
        // initialize start node
        Node start = graph[startRow][startCol];
        start.distance = 0;
        
        // priority queue for Dijkstra
        PriorityQueue<Node> queue = new PriorityQueue<>(
            (a, b) -> Integer.compare(a.distance, b.distance)
        );
        queue.offer(start);
        
        // Dijkstra algorithm loop
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.visited) continue;
            current.visited = true;
            
            // reach target
            if (current.row == targetRow && current.col == targetCol) {
                return reconstructPath(graph[targetRow][targetCol]);
            }
            
            // check neighbors (up, down, left, right)
            int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
            for (int[] dir : directions) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];
                
                if (isValidMove(newRow, newCol) && graph[newRow][newCol] != null) {
                    Node neighbor = graph[newRow][newCol];
                    int newDist = current.distance + 1; // assume each move has a cost of 1
                    
                    if (newDist < neighbor.distance) {
                        neighbor.distance = newDist;
                        neighbor.previous = current;
                        queue.offer(neighbor);
                    }
                }
            }
        }
        
        return null; // no path found
    }

    // reconstruct path from target node to start node
    private List<Point> reconstructPath(Node target) {
        List<Point> path = new ArrayList<>();
        Node current = target;
        while (current != null) {
            path.add(0, new Point(current.row, current.col));
            current = current.previous;
        }
        return path;
    }

    // move ghost with Dijkstra's algorithm
    private void moveGhostWithDijkstra(Mover ghost) {
        // update path every few frames
        if (pathUpdateCounter++ % PATH_UPDATE_INTERVAL == 0) {
            Point start = new Point(ghost.getRow(), ghost.getColumn());
            Point target = new Point(pacman.getRow(), pacman.getColumn());
            List<Point> path = findPathDijkstra(start.x, start.y, target.x, target.y);
            pathCache.put(start, path);
        }
        
        // move ghost along the path
        Point current = new Point(ghost.getRow(), ghost.getColumn());
        List<Point> path = pathCache.get(current);
        if (path != null && path.size() > 1) {
            Point next = path.get(1); // next point in the path
            // set direction based on next point
            if (next.x > current.x) ghost.setDirection(3); // 下 Down
            else if (next.x < current.x) ghost.setDirection(1); // 上 Up
            else if (next.y > current.y) ghost.setDirection(2); // 右 Right
            else if (next.y < current.y) ghost.setDirection(0); // 左 Left
        }
    }

    
    private void moveGhost(Mover ghost) {
        int nextRow = ghost.getNextRow();
        int nextCol = ghost.getNextColumn();
        
        if (isValidMove(nextRow, nextCol)) {
            // restore current position
            Cell currentCell = mazeArray[ghost.getRow()][ghost.getColumn()];
            
            // Ghost on food
            if (currentCell.getItem() == 'G') {  // Ghost on empty cell
                currentCell.setIcon(Icons.BLANK);
                currentCell.setItem(' ');
            } else if (currentCell.getItem() == 'H') {  // Ghost on food
                currentCell.setIcon(Icons.FOOD);
                currentCell.setItem('F');
            }
            
            // move ghost
            ghost.move();
            
            // update new position
            Cell nextCell = mazeArray[nextRow][nextCol];
            if (nextCell.getItem() == 'F') {  // if ghost is on food
                nextCell.setItem('H');  // mark as ghost on food
            } else {
                nextCell.setItem('G');  // mark as ghost position on empty cell
            }
            
            // update display - only if ghost is not eaten
            nextCell.setIcon(isPowered ? Icons.GHOST[0] : ghost.getIcon());
        }
    }
    
    private void checkGhostCollisions() {
        if (ghosts == null) return;
        
        for (Mover ghost : ghosts) {
            if (ghost != null && 
                pacman.getRow() == ghost.getRow() && 
                pacman.getColumn() == ghost.getColumn()) {
                
                if (isPowered) {
                    handleGhostEaten(ghost);
                } else {
                    handleDeath();
                    return;
                }
            }
        }
    }

    private void initializePowerPellets() {
        for (int[] position : POWER_PELLET_POSITIONS) {
            int row = position[0];
            int col = position[1];
            Cell cell = mazeArray[row][col];
            cell.setItem(POWER_PELLET);
            // set power pellet icon
            cell.setIcon(Icons.Cherry); 
        }
    }
    
    private void handleDeath() {
        playSound(deathSound);
        lives--;
        livesLabel.setText("Lives: " + lives);
        messageLabel.setText("Oh no!");
        
        if (lives <= 0) {
            gameOver();
        } else {
            resetPositions();
        }
    }
    
    private void gameOver() {
        pacman.setDead(true);
        mazeArray[pacman.getRow()][pacman.getColumn()].setIcon(Icons.SKULL);
        messageLabel.setText("Game Over!");
        
        String message = String.format("Game Over!\nFinal Score: %d\nGhosts Eaten: %d", 
                                     score, ghostsEaten);
        JOptionPane.showMessageDialog(null, message);
        
        gameTimer.stop();
        ghostTimer.stop();
        cherryTimer.stop();

        long gameTime = (System.currentTimeMillis() - gameStartTime) / 1000;
        String stats = String.format(
            "Game Statistics:\n" +
            "Time Played: %d seconds\n" +
            "Total Score: %d\n" +
            "Ghosts Eaten: %d\n" +
            "Ghost 1: %d times\n" +
            "Ghost 2: %d times\n" +
            "Ghost 3: %d times",
            gameTime, score, ghostsEaten,
            ghostEatenCounts[0], ghostEatenCounts[1], ghostEatenCounts[2]
        );
        
        JOptionPane.showMessageDialog(null, stats);
        saveHighScore();
        
        // Reset game
        StringBuilder highScoreText = new StringBuilder("High Scores:\n");
        for (HighScore hs : highScores) {
            highScoreText.append(String.format("%s: %d\n", hs.name, hs.score));
        }
        JOptionPane.showMessageDialog(null, highScoreText.toString());
    }
    
    private void resetPositions() {
        // Reset Pacman
        mazeArray[pacman.getRow()][pacman.getColumn()].setIcon(Icons.BLANK);
        pacman.setRow(23);
        pacman.setColumn(13);
        pacman.setDirection(0);
        mazeArray[pacman.getRow()][pacman.getColumn()]
            .setIcon(Icons.PACMAN[pacman.getDirection()]);
        
        // Reset ghosts
        ghosts[0].setRow(11); ghosts[0].setColumn(13);
        ghosts[1].setRow(11); ghosts[1].setColumn(11);
        ghosts[2].setRow(11); ghosts[2].setColumn(15);
        
        for (int i = 0; i < ghosts.length; i++) {
            mazeArray[ghosts[i].getRow()][ghosts[i].getColumn()].setIcon(Icons.GHOST[i]);
            mazeArray[ghosts[i].getRow()][ghosts[i].getColumn()].setItem('G');  // 标记Ghost位置
            ghosts[i].setIcon(Icons.GHOST[i]);
        }
        
        // Reset power mode
        isPowered = false;
        powerTimer = 0;
        
        messageLabel.setText("Ready!");
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                togglePause();
                break;
            case KeyEvent.VK_R:
                resetGame();
                break;
            case KeyEvent.VK_M:
                isMuted = !isMuted;
                break;
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_EQUALS:
                if (difficulty < 2) difficulty++;
                updateDifficultyParams();
                break;
            case KeyEvent.VK_MINUS:
                if (difficulty > 0) difficulty--;
                updateDifficultyParams();
                break;
            default:
                super.keyPressed(e);
        }
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            // create a semi-transparent overlay
            JPanel pauseOverlay = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(new Color(0, 0, 0, 128));  // 50% transparent black
                    g.fillRect(0, 0, getWidth(), getHeight());
                    
                    // draw text
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 24));
                    String pauseText = "PAUSED";
                    String resumeText = "Press P to Resume";
                    String difficultyText = "Press +/- to Change Difficulty";
                    
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    
                    g.drawString(pauseText, 
                        centerX - g.getFontMetrics().stringWidth(pauseText)/2, 
                        centerY - 40);
                    g.drawString(resumeText, 
                        centerX - g.getFontMetrics().stringWidth(resumeText)/2, 
                        centerY);
                    g.drawString(difficultyText, 
                        centerX - g.getFontMetrics().stringWidth(difficultyText)/2, 
                        centerY + 40);
                }
            };
            pauseOverlay.setOpaque(false);
            add(pauseOverlay, BorderLayout.CENTER);
            
            gameTimer.stop();
            ghostTimer.stop();
            cherryTimer.stop();
        } else {
            // remove overlay
            for (Component comp : getComponents()) {
                if (comp instanceof JPanel && comp != mazePanel) {
                    remove(comp);
                }
            }
            gameTimer.start();
            ghostTimer.start();
            cherryTimer.start();
        }
        revalidate();
        repaint();
    }

    @Override
    protected void performMove() {
        if (pacman == null || pacman.isDead()) {
            return;
        }

        int nextRow = pacman.getNextRow();
        int nextCol = pacman.getNextColumn();
        
        if (isValidMove(nextRow, nextCol)) {
            Cell currentCell = mazeArray[pacman.getRow()][pacman.getColumn()];
            Cell nextCell = mazeArray[nextRow][nextCol];
            char item = nextCell.getItem();
            
            // process different items
            switch(item) {
                case 'F':  // normal food
                    score += 10;
                    pellets--;  // decrease pellet count
                    playSound(chompSound);
                    break;
                    
                case 'P':  // power pellet
                    score += 50;
                    pellets--;  // decrease power pellet count
                    startPowerMode();
                    playSound(powerSound);
                    // ensure power mode is active
                    nextCell.setItem(' ');
                    break;
                    
                case 'C':  // cherry
                    score += CHERRY_SCORE;
                    playSound(fruitSound);
                    messageLabel.setText("Cherry! +" + CHERRY_SCORE);
                    break;
            }
            
            // update score
            currentCell.setIcon(Icons.BLANK);
            currentCell.setItem(' ');
            
            // move Pacman
            pacman.move();
            nextCell.setIcon(Icons.PACMAN[pacman.getDirection()]);
            nextCell.setItem('P');
        }
    }

    private void startPowerMode() {
        isPowered = true;
        powerTimer = POWER_DURATION;
        ghostsEaten = 0;
        
        // reset all ghosts to uneaten state
        for (Mover ghost : ghosts) {
            if (ghost != null) {
                ghost.setEaten(false);  // reset eaten state
                int row = ghost.getRow();
                int col = ghost.getColumn();
                Cell cell = mazeArray[row][col];
                
                // change ghost appearance
                cell.setIcon(Icons.GHOST[0]);
                ghost.setIcon(Icons.GHOST[0]);
            }
        }
        
        playSound(powerSound);
        messageLabel.setText("Power Mode!");
    }

    private void updatePowerState() {
        if (isPowered) {
            powerTimer--;
            if (powerTimer <= 0) {
                isPowered = false;
                ghostsEaten = 0;
                
                // reset all ghosts to normal color
                for (int i = 0; i < ghosts.length; i++) {
                    if (ghosts[i] != null && !ghosts[i].isEaten()) {  // only reset uneaten ghosts
                        int row = ghosts[i].getRow();
                        int col = ghosts[i].getColumn();
                        Cell cell = mazeArray[row][col];
                        
                        // reset ghost appearance
                        cell.setIcon(Icons.GHOST[i]);
                        ghosts[i].setIcon(Icons.GHOST[i]);
                    }
                }
                
                messageLabel.setText("Power mode ended!");
            } else if (powerTimer <= 50) {  // 50 frames left
                messageLabel.setText("Power mode ending soon!");
            }
        }
    }

    private void handleGhostEaten(Mover ghost) {
        if (!ghost.isEaten()) {  // only eat ghosts that are not already eaten
            ghost.setEaten(true);
            ghostsEaten++;
            score += GHOST_SCORE * ghostsEaten;  // eat more ghosts for higher score
            playSound(ghostSound);
            
            // update ghost stats
            mazeArray[ghost.getRow()][ghost.getColumn()].setIcon(Icons.BLANK);
            
            // respawn ghost after 5 seconds
            Timer respawnTimer = new Timer(5000, e -> {
                if (!isPowered) {  // only respawn if power mode is not active
                    respawnGhost(ghost);
                }
                ((Timer)e.getSource()).stop();
            });
            respawnTimer.start();
            
            messageLabel.setText("Ghost eaten! +" + (GHOST_SCORE * ghostsEaten));
        }
    }

    private void respawnGhost(Mover ghost) {
        // reset ghost position
        ghost.setRow(11);
        ghost.setColumn(13);
        ghost.setEaten(false);
        ghost.setIcon(Icons.GHOST[Arrays.asList(ghosts).indexOf(ghost)]);  // reset ghost icon
        
        // update display
        Cell cell = mazeArray[11][13];
        cell.setIcon(ghost.getIcon());
        cell.setItem('G');
    }
    
    private void updateScore() {
        scoreLabel.setText("Score: " + score);
        if (pellets == 0) {
            handleWin();
        }
    }
    
    private void handleWin() {
        playSound(intermSound);
        String message = String.format(
            "Congratulations! You've won!\nFinal Score: %d\nGhosts Eaten: %d",
            score, ghostsEaten
        );
        JOptionPane.showMessageDialog(null, message);
        gameTimer.stop();
        ghostTimer.stop();
        cherryTimer.stop();
    }
    
    // 修改isValidMove方法以检查是否可以移动到指定位置
    private void loadSounds() {
        try {
            beginningSound = loadClip("../resources/sounds/GAMEBEGINNING.wav");
            chompSound = loadClip("../resources/sounds/pacchomp.wav");
            fruitSound = loadClip("../resources/sounds/fruiteat.wav");
            ghostSound = loadClip("../resources/sounds/GHOSTEATEN.wav");
            deathSound = loadClip("../resources/sounds/killed.wav");
            powerSound = loadClip("../resources/sounds/extrapac.wav");
            intermSound = loadClip("../resources/sounds/interm.wav");
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }
}