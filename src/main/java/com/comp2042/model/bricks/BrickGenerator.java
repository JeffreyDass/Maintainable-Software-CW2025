package com.comp2042.model.bricks;

/**
 * Interface for generating random bricks.
 */
public interface BrickGenerator {

    /**
     * Gets the next brick from the queue.
     *
     * @return a brick object
     */
    Brick getBrick();

    /**
     * Peeks at the next brick without removing it.
     *
     * @return the next brick
     */
    Brick getNextBrick();
}