package com.comp2042.model;

/**
 * Contains information about cleared rows.
 * Stores the number of lines removed, updated matrix, and score bonus.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Creates a new ClearRow result.
     *
     * @param linesRemoved number of lines that were cleared
     * @param newMatrix the updated board matrix after clearing
     * @param scoreBonus points awarded for clearing these lines
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines that were removed.
     *
     * @return number of cleared lines
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets the updated board matrix after row removal.
     * Returns a copy to prevent modification.
     *
     * @return copy of the new matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus for clearing rows.
     * More rows cleared simultaneously = higher bonus.
     *
     * @return score bonus amount
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}