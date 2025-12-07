package com.comp2042.service;

/**
 * Calculates score bonuses for different actions.
 */
public class ScoreService {

    /**
     * Calculates the score bonus for a hard drop.
     * Awards 1 point per row dropped.
     *
     * @param rowsDropped the number of rows the brick fell
     * @return the score bonus
     */
    public static int scoreForHardDrop(int rowsDropped) {
        return rowsDropped;
    }
}