package com.comp2042.model.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * The S-shaped tetromino (zigzag piece).
 * Represented by color ID 5 (Red).
 * Has 2 rotation states, shaped like the letter "S".
 * Forms a zigzag pattern.
 */
final class SBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates an S-brick with its two rotation states.
     * The piece zigzags in different orientations.
     */
    public SBrick() {
        // Horizontal orientation (zigzag left-to-right)
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });

        // Vertical orientation (zigzag top-to-bottom)
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
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