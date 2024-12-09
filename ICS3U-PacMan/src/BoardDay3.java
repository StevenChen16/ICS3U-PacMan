import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

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
    
    public BoardDay3() {
        super();
        initializeGame();
        setupUI();
        setupGhosts();
        setupCherryTimer();
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
        JPanel mazePanel = new JPanel(new GridLayout(25, 27));
        mazePanel.setBackground(Color.BLACK);
        
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
    }
    
    private void setupGhosts() {
        ghosts = new Mover[3];
        int ghostCount = 0;
        
        for (int row = 0; row < 25 && ghostCount < 3; row++) {
            for (int col = 0; col < 27 && ghostCount < 3; col++) {
                char item = mazeArray[row][col].getItem();
                if (item >= '0' && item <= '2') {
                    ghosts[ghostCount] = new Mover(row, col);
                    ghosts[ghostCount].setIcon(Icons.GHOST[ghostCount]);
                    mazeArray[row][col].setIcon(ghosts[ghostCount].getIcon());
                    mazeArray[row][col].setItem('E');
                    ghostCount++;
                }
            }
        }
        
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
            super.actionPerformed(e);
            checkGhostCollisions();
            updateScore();
        }
    }
    
    private void moveGhosts() {
        for (int i = 0; i < ghosts.length; i++) {
            Mover ghost = ghosts[i];
            if (isPowered) {
                moveGhostScared(ghost);
            } else {
                moveGhostNormal(ghost, i);
            }
            moveGhost(ghost);
        }
    }
    
    private void moveGhostScared(Mover ghost) {
        if (random.nextDouble() > 0.3) {
            // Run away from Pacman
            int dx = pacman.getColumn() - ghost.getColumn();
            int dy = pacman.getRow() - ghost.getRow();
            
            if (Math.abs(dx) > Math.abs(dy)) {
                ghost.setDirection(dx > 0 ? 0 : 2);
            } else {
                ghost.setDirection(dy > 0 ? 1 : 3);
            }
        } else {
            ghost.setDirection(random.nextInt(4));
        }
    }
    
    private void moveGhostNormal(Mover ghost, int ghostType) {
        if (random.nextDouble() > 0.3) {
            switch (ghostType) {
                case 0: // Direct chaser
                    chaseDirectly(ghost);
                    break;
                case 1: // Predictor
                    predictAndChase(ghost);
                    break;
                case 2: // Random but aggressive
                    chaseWithRandomness(ghost);
                    break;
            }
        } else {
            ghost.setDirection(random.nextInt(4));
        }
    }
    
    private void chaseDirectly(Mover ghost) {
        // Directly chase Pacman
        int dx = pacman.getColumn() - ghost.getColumn();
        int dy = pacman.getRow() - ghost.getRow();
        
        if (Math.abs(dx) > Math.abs(dy)) {
            ghost.setDirection(dx > 0 ? 2 : 0);
        } else {
            ghost.setDirection(dy > 0 ? 3 : 1);
        }
    }
    
    private void predictAndChase(Mover ghost) {
        // Try to predict where Pacman will be
        int predictedX = pacman.getColumn() + (pacman.getdColumn() * 2);
        int predictedY = pacman.getRow() + (pacman.getdRow() * 2);
        
        int dx = predictedX - ghost.getColumn();
        int dy = predictedY - ghost.getRow();
        
        if (Math.abs(dx) > Math.abs(dy)) {
            ghost.setDirection(dx > 0 ? 2 : 0);
        } else {
            ghost.setDirection(dy > 0 ? 3 : 1);
        }
    }
    
    private void chaseWithRandomness(Mover ghost) {
        // Chase but with more randomness
        if (random.nextDouble() > 0.5) {
            chaseDirectly(ghost);
        } else {
            ghost.setDirection(random.nextInt(4));
        }
    }
    
    private void moveGhost(Mover ghost) {
        int nextRow = ghost.getNextRow();
        int nextCol = ghost.getNextColumn();
        int currentRow = ghost.getRow();
        int currentCol = ghost.getColumn();
        
        if (isValidMove(nextRow, nextCol)) {
            // 保存当前位置的状态
            Cell currentCell = mazeArray[currentRow][currentCol];
            char currentItem = currentCell.getItem();
            
            // 恢复当前位置的原始状态
            if (currentItem == 'G') {  // Ghost在空地上
                currentCell.setIcon(Icons.BLANK);
                currentCell.setItem(' ');
            } else if (currentItem == 'H') {  // Ghost在食物上
                currentCell.setIcon(Icons.FOOD);
                currentCell.setItem('F');
            }
            
            // 移动ghost
            ghost.move();
            
            // 保存新位置的原始状态并更新
            Cell nextCell = mazeArray[nextRow][nextCol];
            char nextItem = nextCell.getItem();
            
            if (nextItem == 'F') {  // 如果新位置有食物
                nextCell.setItem('H');  // 标记为Ghost在食物上
            } else {
                nextCell.setItem('G');  // 标记为Ghost在空地上
            }
            
            // 设置Ghost外观 - Power模式下统一使用GHOST[0]
            nextCell.setIcon(isPowered ? Icons.GHOST[0] : ghost.getIcon());
        }
    }
    
    private void checkGhostCollisions() {
        for (Mover ghost : ghosts) {
            if (pacman.getRow() == ghost.getRow() && 
                pacman.getColumn() == ghost.getColumn()) {
                if (isPowered) {
                    handleGhostEaten(ghost);
                } else {
                    handleDeath();
                }
                break;
            }
        }
    }
    
    private void handleGhostEaten(Mover ghost) {
        ghostsEaten++;
        score += GHOST_SCORE * ghostsEaten;
        playSound(ghostSound);
        
        // Reset ghost position
        ghost.setRow(11);
        ghost.setColumn(13);
        
        messageLabel.setText("Ghost eaten! +" + (GHOST_SCORE * ghostsEaten));
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
    
    private void updatePowerState() {
        if (isPowered) {
            powerTimer--;
            if (powerTimer <= 0) {
                isPowered = false;
                ghostsEaten = 0;
                
                // 恢复所有Ghost的正常状态
                for (int i = 0; i < ghosts.length; i++) {
                    int row = ghosts[i].getRow();
                    int col = ghosts[i].getColumn();
                    Cell cell = mazeArray[row][col];
                    
                    // 恢复原始颜色
                    cell.setIcon(Icons.GHOST[i]);
                    ghosts[i].setIcon(Icons.GHOST[i]);
                }
                
                messageLabel.setText("Power mode ended!");
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            togglePause();
        } else {
            super.keyPressed(e);
            if (!gameTimer.isRunning()) {
                playSound(chompSound);
            }
        }
    }
    
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            gameTimer.stop();
            ghostTimer.stop();
            cherryTimer.stop();
            messageLabel.setText("PAUSED");
        } else {
            gameTimer.start();
            ghostTimer.start();
            cherryTimer.start();
            messageLabel.setText("Game On!");
        }
    }
    
    @Override
    protected void performMove() {
        if (pacman == null || pacman.isDead()) {
            return;
        }

        int nextRow = pacman.getNextRow();
        int nextCol = pacman.getNextColumn();
        
        if (isValidMove(nextRow, nextCol)) {
            Cell nextCell = mazeArray[nextRow][nextCol];
            
            // Check for special items
            if (nextCell.getItem() == 'C') { // Cherry
                score += CHERRY_SCORE;
                playSound(fruitSound);
                messageLabel.setText("Cherry! +" + CHERRY_SCORE);
            } else if (nextCell.getItem() == 'F') {
                playSound(chompSound);
                if (random.nextDouble() < 0.1) { // 10% chance to enter power mode from normal food
                    startPowerMode();
                }
            }
            
            super.performMove(); // Perform the actual movement
        }
    }
    
    private void startPowerMode() {
        isPowered = true;
        powerTimer = POWER_DURATION;
        ghostsEaten = 0;
        
        // 更新所有Ghost的状态
        for (Mover ghost : ghosts) {
            int row = ghost.getRow();
            int col = ghost.getColumn();
            Cell cell = mazeArray[row][col];
            
            // Power模式下统一使用GHOST[0]表示vulnerable状态
            cell.setIcon(Icons.GHOST[0]);
            ghost.setIcon(Icons.GHOST[0]);
        }
        
        playSound(powerSound);
        messageLabel.setText("Power Mode!");
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
    
    // 修改loadSounds方法使用正确的路径
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