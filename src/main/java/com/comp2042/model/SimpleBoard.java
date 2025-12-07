package com.comp2042.model;

import com.comp2042.model.bricks.Brick;
import com.comp2042.model.bricks.BrickGenerator;
import com.comp2042.model.bricks.RandomBrickGenerator;
import com.comp2042.util.BrickRotator;

import java.awt.Point;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the game board.
 * Manages the game matrix, current brick, and game state.
 * Handles brick movement, rotation, collision detection, and scoring.
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private Point ghostBrickOffset;
    private final Score score;
    private Brick heldBrick = null;
    private boolean holdUsed = false;
    private final Queue<Brick> nextPieces = new ArrayDeque<>();

    /**
     * Creates a new game board with specified dimensions.
     * Initializes the game matrix, brick generator, and fills the next piece queue.
     *
     * @param width the width of the board in blocks
     * @param height the height of the board in blocks
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();

        // Pre-fill the next piece queue with 3 pieces
        for (int i = 0; i < 3; i++) {
            nextPieces.add(brickGenerator.getBrick());
        }
    }

    /**
     * Gets the currently held brick.
     *
     * @return the held brick, or null if none
     */
    public Brick getHeldBrick() {
        return heldBrick;
    }

    /**
     * Attempts to move the current brick down by one row.
     *
     * @return true if the brick moved successfully, false if it hit something
     */
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);

        // Check if moving down would cause a collision
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());

        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            updateGhostBrick();
            return true;
        }
    }

    /**
     * Attempts to move the current brick left by one column.
     *
     * @return true if the brick moved successfully, false if blocked
     */
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);

        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());

        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            updateGhostBrick();
            return true;
        }
    }

    /**
     * Attempts to move the current brick right by one column.
     *
     * @return true if the brick moved successfully, false if blocked
     */
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);

        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());

        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            updateGhostBrick();
            return true;
        }
    }

    /**
     * Attempts to rotate the current brick counterclockwise.
     * Uses wall kick system to allow rotation near edges.
     *
     * @return true if rotation was successful, false if blocked
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();

        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());

        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            updateGhostBrick();
            return true;
        }
    }

    /**
     * Swaps the current brick with the held brick.
     * If no brick is held, stores current and spawns new one.
     * Can only be used once per brick.
     *
     * @return updated view data for rendering
     */
    @Override
    public ViewData holdBrick() {

        // If player already used hold this turn, don't allow it again
        if (holdUsed) {
            return getViewData();
        }

        if (heldBrick == null) {
            // First time holding: store current brick and spawn a new one
            heldBrick = brickRotator.getCurrentBrick();
            createNewBrick();
        } else {
            // Swap the held brick with current brick
            Brick temp = brickRotator.getCurrentBrick();
            brickRotator.setBrick(heldBrick);
            heldBrick = temp;

            // Reset spawn position after swap
            currentOffset = new Point(4, 1);
        }

        holdUsed = true;
        updateGhostBrick();
        return getViewData();
    }

    /**
     * Instantly drops the brick to the lowest possible position.
     *
     * @return number of rows the brick dropped
     */
    @Override
    public int hardDrop() {
        int rowsDropped = ghostBrickOffset.y - currentOffset.y;

        // Move brick to ghost position
        currentOffset = new Point(ghostBrickOffset);

        updateGhostBrick();

        return rowsDropped;
    }

    /**
     * Updates the ghost brick position to show where the brick will land.
     *
     * @return true if update was successful
     */
    private boolean updateGhostBrick() {
        Point p = calculateGhostBrickOffset(currentOffset);
        ghostBrickOffset = p;
        return true;
    }

    /**
     * Calculates where the brick would land if dropped straight down.
     * Simulates downward movement until collision is detected.
     *
     * @param num the current brick position
     * @return the lowest valid position for the brick
     */
    private Point calculateGhostBrickOffset(Point num) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(num);

        // Keep moving down until we hit something
        while(true) {
            p.setLocation(currentOffset.getX(), p.getY() + 1);
            boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
            if (conflict) {
                // Move back up one row (last valid position)
                p.setLocation(p.getX(), p.getY() - 1);
                return p;
            }
        }
    }

    /**
     * Creates and spawns a new brick at the top of the board.
     * Takes next brick from queue and generates a new one to add to queue.
     *
     * @return true if the new brick overlaps existing blocks (game over), false otherwise
     */
    @Override
    public boolean createNewBrick() {

        // Take the next brick from the queue
        Brick currentBrick = nextPieces.poll();
        brickRotator.setBrick(currentBrick);

        // Generate a new brick and add it to the queue
        nextPieces.add(brickGenerator.getBrick());

        // Reset hold usage for the new brick
        holdUsed = false;

        // Spawn position (centered horizontally, near top)
        currentOffset = new Point(4, 1);

        ghostBrickOffset = calculateGhostBrickOffset(currentOffset);

        // Check if spawn position causes game over
        return MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y
        );
    }

    /**
     * Gets the current game board matrix.
     *
     * @return 2D array representing the board state
     */
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Gets the current view data for rendering.
     * Includes brick position, ghost position, and next brick.
     *
     * @return ViewData containing all rendering information
     */
    @Override
    public ViewData getViewData() {

        // Get the next brick's shape from queue (without removing it)
        Brick next = nextPieces.peek();
        int[][] nextShape = next.getShapeMatrix().get(0);

        return new ViewData(
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y,
                ghostBrickOffset.x,
                ghostBrickOffset.y,
                nextShape
        );
    }

    /**
     * Gets the shapes of all bricks in the next queue.
     * Used to display the preview of upcoming pieces.
     *
     * @return list of brick shape matrices
     */
    @Override
    public List<int[][]> getNextQueueShapes() {
        List<int[][]> list = new ArrayList<>();
        for (Brick b : nextPieces) {
            // Get first rotation state of each brick
            list.add(b.getShapeMatrix().get(0));
        }
        return list;
    }

    /**
     * Merges the current brick into the background board.
     * Called when a brick lands and becomes part of the board.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY());

        // Reset hold after merge
        holdUsed = false;
    }

    /**
     * Checks for and removes any completed rows.
     * Updates the game matrix with rows removed.
     *
     * @return ClearRow object containing number of lines cleared and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    /**
     * Gets the score object for this board.
     *
     * @return the Score object
     */
    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Gets the shape of the currently held brick.
     *
     * @return brick shape matrix, or null if no brick is held
     */
    @Override
    public int[][] getHeldBrickShape() {
        if (heldBrick == null) return null;
        // Return first rotation state
        return heldBrick.getShapeMatrix().get(0);
    }

    /**
     * Resets the hold state for a new game.
     * Clears the held brick and allows holding again.
     */
    @Override
    public void resetHold() {
        heldBrick = null;
        holdUsed = false;
    }

    /**
     * Starts a new game by resetting the board.
     * Clears the matrix, resets score, and creates a new brick.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}