package com.comp2042.model;

/**
 * Contains information about the next rotation state of a brick.
 * Used when rotating pieces to check validity.
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Creates info about the next shape rotation.
     *
     * @param shape the 2D array representing the rotated shape
     * @param position the rotation index
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets the shape matrix for the next rotation.
     * Returns a copy to prevent modification.
     *
     * @return copy of the shape matrix
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the rotation position index.
     *
     * @return rotation index
     */
    public int getPosition() {
        return position;
    }
}