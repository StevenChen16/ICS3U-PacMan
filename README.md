# ICS3U Assignment - PacMan Game Implementation

## Overview

This project is a Java implementation of the classic PacMan game, developed as part of the ICS3U course assignment. The game features classic PacMan mechanics with some unique additions, built using Java Swing for the graphical user interface.

## Development Environment

- Language: Java
- GUI Framework: Java Swing
- IDE: Any Java IDE (developed and tested with Visual Studio Code)
- Required JDK Version: 8 or higher

## Game Features

### Core Mechanics
- Classic PacMan movement and pellet collection
- Three ghosts with different AI behaviors
- Wall collision detection
- Score tracking system
- Lives system (3 lives initially)

### Special Features
- Power Mode: Activated randomly (10% chance) when eating pellets
  - Enables PacMan to eat ghosts for bonus points
  - Ghosts change appearance and try to escape
  - Points multiply for consecutive ghost captures
- Cherry System: Special bonus item that appears periodically
- Sound Effects: Various sound effects for different game events
- Score System:
  - 10 points for each pellet
  - 100 points for cherry
  - 200 points for each ghost (multiplies with consecutive captures)

## Game Controls

- Arrow Keys: Move PacMan
- P: Pause/Resume game
- Border Teleportation: PacMan can move through borders to appear on the opposite side

## Project Structure

```
src/
│  PacManGame.java       # Main game entry point
│  PacManGUI.java        # Main game window setup
│  BoardDay1.java        # Basic game board implementation
│  BoardDay2.java        # Added movement and collision
│  BoardDay3.java        # Added ghosts and power mode
│  Cell.java            # Individual cell representation
│  Icons.java           # Game resource management
│  Mover.java           # Base class for moving objects
│  MenuGUI.java         # Menu GUI Frame
│
resources/
│  └─images/            # Game sprites and icons
│  └─sounds/            # Game sound effects
│  maze.txt                # Maze layout definition
```

## Technical Highlights

1. Object-Oriented Design
   - Inheritance hierarchy for game boards (BoardDay1 → BoardDay2 → BoardDay3)
   - Encapsulation of game elements (Cell, Mover, Node classes)
   - Clear separation of concerns between UI, game logic, and menu system
   - Modular design for easy extension and modification

2. Ghost AI Implementation
   - Three distinct ghost personalities with unique behaviors:
     * Blinky (Direct Chase): Follows PacMan directly
     * Pinky (Predictive Chase): Attempts to intercept PacMan
     * Inky (Random Aggressive): Combines random movement with chase behavior
   - State-based behavior system:
     * Normal state: Unique pursuit strategies
     * Vulnerable state: Escape behavior during power mode
     * Respawn mechanism after being eaten
   - Prepared framework for Dijkstra pathfinding implementation
   - Configurable AI difficulty levels

3. Power Mode System
   - Strategic power pellet placement in maze corners
   - Complete power mode lifecycle management:
     * Activation through power pellets
     * Ghost vulnerability state handling
     * Score multiplier for eating ghosts
     * Timed duration with visual countdown
   - Cherry bonus item system

4. User Interface Design
   - Multi-language support (English/Chinese)
   - Comprehensive menu system:
     * Main menu with game start, instructions, and exit options
     * In-game pause menu with difficulty settings
     * Detailed bilingual instruction panel
   - Dynamic score and status display
   - Visual feedback for game states

5. Event Handling & Resource Management
   - Sophisticated keyboard input processing
   - Collision detection system
   - Centralized resource loading
   - Efficient sprite and sound management
   - Game state persistence

## Learning Outcomes

Through this project, I demonstrated:
- Advanced Java OOP principles including inheritance, polymorphism, and encapsulation
- GUI programming expertise with Java Swing
- Game development concepts including:
  * Complex collision detection systems
  * Multi-layered AI behavior implementation
  * Comprehensive game state management
  * Efficient resource handling and caching
- Multi-language support implementation
- Menu system design and user interface planning
- Game resource organization and management
- Problem-solving skills in implementing complex game mechanics
- Clean code practices and project structure planning

## Future Improvements

Potential areas for enhancement:
- Implementation of Dijkstra's pathfinding for advanced ghost AI
- Additional maze layouts and level progression
- Enhanced visual effects and animations
- Customizable control schemes
- Online leaderboard system
- Achievement system
- Level editor for custom maze creation
- Additional power-up types and game mechanics
- Network multiplayer capability
- Mobile touch controls support

## Repository

This project is hosted on GitHub: https://github.com/StevenChen16/ICS3U-PacMan.git