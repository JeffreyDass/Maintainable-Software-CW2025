package com.comp2042.model.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * The J-shaped tetromino.
 * Represented by color ID 2 (Blue Violet).
 * Has 4 rotation states, shaped like the letter "J".
 */
final class JBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Creates a J-brick with its four rotation states.
     * The brick has a distinctive L-shape that curves to the left.
     */
    public JBrick() {
        // First rotation: hook pointing down-left
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        });

        // Second rotation: hook pointing up-left
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0}
        });

        // Third rotation: hook pointing up-right
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0}
        });

        // Fourth rotation: hook pointing down-right
        brickMatrix.add(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
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