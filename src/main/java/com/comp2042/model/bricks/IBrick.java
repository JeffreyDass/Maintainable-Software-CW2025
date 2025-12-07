package com.comp2042.model.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * The I-shaped tetromino (straight line piece).
 * Represented by color ID 1 (Aqua/Cyan).
 * Has 2 rotation states: horizontal and vertical.
 * This is the only piece that can clear 4 lines at once (Tetris).
 */
final class IBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates an I-brick with its two rotation states.
     * Horizontal state spans 4 columns, vertical state spans 4 rows.
     */
    public IBrick() {
        // Horizontal orientation (flat)
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        // Vertical orientation (standing)
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }

    /**
     * Gets all rotation states of this brick.
     *
     * @return list containing 2 rotation states
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}