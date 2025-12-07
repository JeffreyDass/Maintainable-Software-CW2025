package com.comp2042.model;

import java.util.List;

/**
 * Interface representing the game board.
 * Defines all operations that can be performed on the board.
 */
public interface Board {

    /**
     * Moves the current brick down by one row.
     *
     * @return true if movement was successful, false if blocked
     */
    boolean moveBrickDown();

    /**
     * Moves the current brick left by one column.
     *
     * @return true if movement was successful, false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Moves the current brick right by one column.
     *
     * @return true if movement was successful, false if blocked
     */
    boolean moveBrickRight();

    /**
     * Rotates the current brick counterclockwise.
     *
     * @return true if rotation was successful, false if blocked
     */
    boolean rotateLeftBrick();

    /**
     * Creates a new brick at the top of the board.
     *
     * @return true if the new brick overlaps existing blocks (game over), false otherwise
     */
    boolean createNewBrick();

    /**
     * Gets the current board matrix showing all placed blocks.
     *
     * @return 2D array representing the board state
     */
    int[][] getBoardMatrix();

    /**
     * Gets the current view data for rendering the falling brick.
     *
     * @return view data containing brick position and shape
     */
    ViewData getViewData();

    /**
     * Gets the shapes of upcoming bricks in the queue.
     *
     * @return list of brick shape matrices
     */
    List<int[][]> getNextQueueShapes();

    /**
     * Swaps the current brick with the held brick.
     *
     * @return updated view data
     */
    ViewData holdBrick();

    /**
     * Resets the hold state for a new game.
     */
    void resetHold();

    /**
     * Gets the shape of the currently held brick.
     *
     * @return brick shape matrix, or null if no brick is held
     */
    int[][] getHeldBrickShape();

    /**
     * Instantly drops the brick to the lowest possible position.
     *
     * @return number of rows the brick dropped
     */
    int hardDrop();

    /**
     * Merges the current brick into the background board.
     */
    void mergeBrickToBackground();

    /**
     * Checks for and removes any completed rows.
     *
     * @return information about cleared rows and score bonus
     */
    ClearRow clearRows();

    /**
     * Gets the current score object.
     *
     * @return the game score
     */
    Score getScore();

    /**
     * Starts a new game by resetting the board.
     */
    void newGame();
}

