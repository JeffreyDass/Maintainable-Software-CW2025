package com.comp2042.model.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * The Z-shaped tetromino (reverse zigzag piece).
 * Represented by color ID 7 (Burlywood).
 * Has 2 rotation states, shaped like the letter "Z".
 * Forms a reverse zigzag pattern (opposite of S-piece).
 */
final class ZBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a Z-brick with its two rotation states.
     * The piece zigzags in the opposite direction from the S-piece.
     */
    public ZBrick() {
        // Horizontal orientation (zigzag right-to-left)
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0}
        });

        // Vertical orientation (zigzag top-to-bottom, reversed)
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
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