package com.comp2042.service;

/**
 * Calculates the current level based on lines cleared.
 */
public class LevelService {

    /**
     * Determines the current level based on total lines cleared.
     *
     * @param totalLines the total number of lines cleared
     * @param linesPerLevel how many lines are needed per level
     * @return the current level number
     */
    public static int calculateLevel(int totalLines, int linesPerLevel) {
        return 1 + (totalLines / linesPerLevel);
    }
}