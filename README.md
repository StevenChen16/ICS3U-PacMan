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
│
resources/
│  └─images/            # Game sprites and icons
│  └─sounds/            # Game sound effects
│  maze.txt                # Maze layout definition
```

## Technical Highlights

1. Object-Oriented Design
   - Inheritance hierarchy for game boards (BoardDay1 → BoardDay2 → BoardDay3)
   - Encapsulation of game elements (Cell, Mover classes)
   - Clear separation of concerns between UI and game logic

2. Ghost AI Implementation
   - Three different ghost behaviors:
     * Direct Chase: Follows PacMan directly
     * Predictive Chase: Attempts to intercept PacMan
     * Random Aggressive: Combines random movement with chase behavior
   - Different behavior in normal and vulnerable states

3. Event Handling
   - Keyboard input processing
   - Collision detection
   - Game state management

4. Resource Management
   - Centralized resource loading
   - Efficient sprite management
   - Sound effect integration

## Learning Outcomes

Through this project, I demonstrated:
- Understanding of Java OOP principles
- GUI programming skills with Java Swing
- Game development concepts including:
  * Collision detection
  * AI behavior implementation
  * Game state management
  * Resource handling
- Problem-solving skills in implementing complex game mechanics
- Code organization and project structure planning

## Future Improvements

Potential areas for enhancement:
- Additional levels with different layouts
- High score system
- More sophisticated ghost AI
- Power pellet items instead of random activation
- Animation improvements

## Repository

This project is hosted on GitHub: https://github.com/StevenChen16/ICS3U-PacMan.git