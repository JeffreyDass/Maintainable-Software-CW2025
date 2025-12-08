# COMP2042 Coursework - Tetris Game Maintenance

**Student Name:** Jeffrey Teoh Dass  
**Student ID:** 20607904
**Academic Year:** 2025/2026

---

## GitHub Repository

🔗 **https://github.com/JeffreyDass/Maintainable-Software-CW2025**

---

## Compilation Instructions

### Prerequisites
- Java 21/23
- Maven
- IntelliJ IDEA

### Steps to Run
1. Clone the repository
2. Open project in IntelliJ IDEA
3. Run `mvn clean install`
4. Run `Main.java` or use `mvn javafx:run`

### Controls
- **Arrow Keys / WASD:** Move and rotate
- **Space:** Hard drop
- **C:** Hold piece
- **ESC:** Pause / Unpause
- **N:** New game

---

## Implemented and Working Properly

### Refactoring (35%)

1. **Package Reorganization** - Restructured from flat package to proper MVC architecture with `app`, `config`, `controller`, `model`, `service`, `media`, `util`, `view` packages

2. **Configuration Extraction** - Created `GameConfig.java` to centralize all constants (window size, brick size, level progression)

3. **Service Layer** - Added `ScoreService` and `LevelService` to separate business logic from game mechanics

4. **Enhanced Documentation** - Added comprehensive Javadoc to all 34 classes with method descriptions, parameters, and return values

5. **Encapsulation** - Made all brick classes package-private, improved field visibility throughout codebase

6. **Bug Fix** - Fixed spawn position from `Point(4, 10)` to `Point(4, 1)` for proper top-of-board spawning

### New Features (25%)

1. **Ghost Piece** - Semi-transparent preview showing where piece will land, updates in real-time

2. **Hold Mechanic** - Press C to store current piece for later use (once per piece)

3. **3-Piece Preview** - Shows next 3 pieces instead of 1 for better planning

4. **Hard Drop** - Space bar instantly drops piece with bonus scoring (1 point per row)

5. **Progressive Difficulty** - Speed increases every 10 lines cleared with level display

6. **Enhanced Scoring** - Exponential scoring (50×n²) rewards multi-line clears

7. **Pause System** - ESC key pauses game with menu overlay (Resume/New Game/Quit)

8. **Background Media** - Looping video background and Tetris music

9. **UI Improvements** - Score, lines, and level counters; improved window size (500×510)

---

## Implemented but Not Working Properly

None - all features are fully functional.

---

## Features Not Implemented

- **T-Spin Detection** - Orignally wanted to make it so that the block would not immediately be connected to background to allow players to do advanced techniques like T-Spin and make the game feel better to play but complex collision analysis was required
- **7-Bag Randomizer** - Wanted to add a system to make the randomiser more fair cause it feels bad to pull the same brick 3-4 times in a row but could not add it in time
- **Wall Kick System** - To make the game feel more respondive I wanted to allow the block on the corner and have the block more left or right but that would need extensive rotation tables which I did not how to add yet
- **Dynamic Resolution** - Different Resolution might make assets not aligned so i wanted to make the game adjust dynamically but did not test enough.

---

## New Java Classes

1. `config/GameConfig.java` - Configuration constants
2. `service/ScoreService.java` - Score calculation logic
3. `service/LevelService.java` - Level progression logic
4. `media/BackgroundMediaService.java` - Video player factory
5. `media/BackgroundMusicService.java` - Music player factory

---

## Modified Java Classes

### Major Changes

**`model/SimpleBoard.java`**
- Changes: 
    - Added ghost brick calculation (`calculateGhostBrickOffset()`) 
    - Added hold mechanic (`holdBrick()`, `heldBrick`, `holdUsed`)
    - Added hard drop (`hardDrop()`)
    - Changed to 3-piece queue (`nextPieces` Queue)
    - Fixed spawn position bug
- Reason: Core game logic implementation for all new features; improved architecture following Single Responsibility Principle

**`controller/GuiController.java`**
- Changes:
    - Added ghost brick rendering (`ghostBrickPanel`)
    - Added hold UI panel (`holdPanel`)
    - Added 3 next piece panels
    - Added pause overlay system
    - Added level/speed progression
    - Added score/lines/level labels
    - Integrated background video and music
    - Added ESC, Space, and C key handlers
- Reason: Support all new visual features, improve UX, better state management

**`controller/GameController.java`**
- Changes: 
    - Added `onHoldEvent()` and `onHardDropEvent()` methods
    - Integrated new scoring system
- Reason: Support new game features, improve abstraction, separate concerns

**`model/ViewData.java`**
- Changes:
   - Added ghost position fields (`ghostxPosition`, `ghostyPosition`)
- Reason: Enable ghost piece rendering by passing position data to view layer without violating encapsulation

**`model/Board.java` Interface**
- Changes:
   - Added methods: `getNextQueueShapes()`, `holdBrick()`, `resetHold()`, `getHeldBrickShape()`, `hardDrop()`
- Reason: Define contract for new board operations; support Dependency Inversion Principle by programming to interface

### Minor Changes

All classes moved to proper packages with added Javadoc documentation. All brick classes made package-private for better encapsulation.

---

## Unexpected Problems

1. **Music Not Pausing** - Music would continue playing after game over or in pause menu. Fixed by stopping and replaying the music at every condition.

2. **Hold Exploit** - Players could keep a piece in hold after starting a new game. Fixed with `resetHold()` function that resets the heldBrick to null.

3. **Spawn Position** - Original `Point(4, 10)` was too low. Changed to `Point(4, 1)`.

4. **Maven not running** - Running the program through maven did not work. Fixed by changing the pom.xml to use jdk21.

5. **Set Window Size** - The player could originally adjust the window size which would bug the position. Fixed by using `primaryStage.setFullScreen(false);`.

---

## Design Patterns Used

- **MVC** - Separation of Model, View, Controller
- **Strategy** - `BrickGenerator` interface
- **Observer** - Score property binding
- **Factory** - Media player creation
- **Interface Segregation** - `Board`, `Brick`, `InputEventListener` interfaces

---

## AI Use Declaration

**[Choose one:]**

*Option 1: If you used AI*
- AI Tools Used: [e.g., GitHub Copilot, ChatGPT]
- Usage: [Be specific - e.g., "Used for Javadoc generation and debugging"]

*Option 2: If no AI used*
- No generative AI tools were used in this coursework.

---

**Submission Date:** December 8th, 2025