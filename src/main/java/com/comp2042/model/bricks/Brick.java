package com.comp2042.model.bricks;

import java.util.List;

/**
 * Interface for all brick types.
 * Each brick has multiple rotation states.
 */
public interface Brick {

    /**
     * Gets all rotation states of this brick.
     *
     * @return list of 2D arrays, each representing a rotation state
     */
    List<int[][]> getShapeMatrix();
}