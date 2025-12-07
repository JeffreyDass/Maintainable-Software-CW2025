package com.comp2042.model.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * The T-shaped tetromino.
 * Represented by color ID 6 (Beige).
 * Has 4 rotation states, shaped like the letter "T".
 * The most versatile piece for filling gaps.
 */
final class TBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a T-brick with its four rotation states.
     * The piece has a T-shape that rotates 90 degrees each time.
     */
    public TBrick() {
        // First rotation: T pointing up
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {6, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });

        // Second rotation: T pointing right
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {0, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });

        // Third rotation: T pointing down
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 6, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        // Fourth rotation: T pointing left
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 0, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Gets all rotation states of this brick.
     *
     * @return list containing 4 rotation states
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}