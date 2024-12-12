/*
 * Name: Yucheng Chen
 * Date: December 12, 2024
 * Course Code: ICS3U1-03 Mr.Fernandes
 * Title: PacMan Game
 * 
 * Description:
 * A Java implementation of the classic PacMan game with modern features.
 * The game includes a bilingual menu system (English/Chinese), three different
 * ghost AI behaviors, power-up mechanics, and various game modes. Players control
 * PacMan to eat dots while avoiding ghosts, with the option to eat ghosts during
 * power mode. The game features a complete scoring system, difficulty settings,
 * and sound effects.
 * 
 * Major Skills:
 * - Object-Oriented Programming (inheritance, polymorphism, encapsulation)
 * - Swing GUI Components (JFrame, JPanel, JButton, JLabel)
 * - Event Handling (KeyListener, ActionListener)
 * - Collections Framework (ArrayList, HashMap)
 * - File I/O Operations (sound and image loading)
 * - Timer and Animation Control
 * - Resource Management
 * - Multi-threading (SwingUtilities)
 * - State Management
 * - Game Loop Implementation
 * 
 * Added Features:
 * 1. Bilingual Support (English/Chinese)
 *    - Complete menu system in both languages
 *    - In-game instructions and UI elements
 * 
 * 2. Advanced Ghost AI
 *    - Three distinct ghost personalities with unique behaviors
 *    - State-based behavior system (normal/vulnerable states)
 *    - Framework prepared for Dijkstra pathfinding
 * 
 * 3. Complete Power Mode System
 *    - Strategic power pellet placement
 *    - Ghost vulnerability handling
 *    - Score multiplier system
 * 
 * 4. Enhanced Sound System
 *    - Game start sound
 *    - Pellet eating sounds
 *    - Power mode sounds
 *    - Ghost eating sounds
 *    - Death sound
 *    - Victory sound
 * 
 * 5. Menu and UI Features
 *    - Main menu system
 *    - In-game pause menu
 *    - Difficulty settings
 *    - Score display
 *    - Lives counter
 *    - Game timer
 * 
 * 6. Bonus Features
 *    - Cherry bonus system
 *    - High score tracking
 *    - Multiple difficulty levels
 * 
 * Areas of Concern:
 * 1. Sound file paths may need adjustment depending on deployment environment
 * 2. Ghost AI could be further optimized with Dijkstra pathfinding implementation
 * 3. Some visual effects could be enhanced (e.g., death animation)
 * 4. Resource loading might need optimization for different environments
 */

public class PacManGame {
    public static void main(String[] args) {
        // Create a new instance of the game GUI
        new MenuGUI();
    }
}