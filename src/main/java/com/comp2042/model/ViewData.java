package com.comp2042.model;

/**
 * Contains all data needed to render the current game state.
 * Includes brick position, ghost position, and next brick preview.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostxPosition;
    private final int ghostyPosition;
    private final int[][] nextBrickData;

    /**
     * Creates view data for rendering.
     *
     * @param brickData the current brick's shape matrix
     * @param xPosition x coordinate of the brick
     * @param yPosition y coordinate of the brick
     * @param ghostxPosition x coordinate of the ghost brick
     * @param ghostyPosition y coordinate of the ghost brick
     * @param nextBrickData the next brick's shape matrix
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition,
                    int ghostxPosition, int ghostyPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostxPosition = ghostxPosition;
        this.ghostyPosition = ghostyPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Gets the current brick's shape data.
     * Returns a copy to prevent modification.
     *
     * @return copy of brick shape matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the x position of the brick.
     *
     * @return x coordinate
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the y position of the brick.
     *
     * @return y coordinate
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets the x position of the ghost brick.
     *
     * @return ghost x coordinate
     */
    public int getGhostxPosition() {
        return ghostxPosition;
    }

    /**
     * Gets the y position of the ghost brick.
     *
     * @return ghost y coordinate
     */
    public int getGhostyPosition() {
        return ghostyPosition;
    }

    /**
     * Gets the next brick's shape data.
     * Returns a copy to prevent modification.
     *
     * @return copy of next brick shape matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
