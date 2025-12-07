package com.comp2042.model.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * The O-shaped tetromino (square piece).
 * Represented by color ID 4 (Yellow).
 * Has only 1 rotation state since it's a 2x2 square.
 * This is the only piece that doesn't change when rotated.
 */
final class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates an O-brick with its single rotation state.
     * The piece is a 2x2 square that looks the same in all rotations.
     */
    public OBrick() {
        // Only one state - it's a square
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Gets all rotation states of this brick.
     *
     * @return list containing 1 rotation state (since square doesn't change)
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}