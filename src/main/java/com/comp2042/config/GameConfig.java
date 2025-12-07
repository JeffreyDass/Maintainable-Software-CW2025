package com.comp2042.config;

/**
 * Stores all game configuration constants.
 * Contains settings for window size, brick size, and level progression.
 */
public final class GameConfig {

    /** Width of the game window in pixels */
    public static final int WINDOW_WIDTH = 500;

    /** Height of the game window in pixels */
    public static final int WINDOW_HEIGHT = 510;

    /** Size of each brick block in pixels */
    public static final int BRICK_SIZE = 20;

    /** Number of lines needed to advance to the next level */
    public static final int LINES_PER_LEVEL = 10;

    // Private constructor to prevent instantiation
    private GameConfig() { }
}