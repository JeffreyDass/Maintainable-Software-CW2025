package com.comp2042.util;

import com.comp2042.model.bricks.Brick;
import com.comp2042.model.NextShapeInfo;

/**
 * Manages brick rotation states.
 * Keeps track of current brick and its rotation.
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Gets the next rotation state of the current brick.
     *
     * @return information about the next shape
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Gets the current rotation state of the brick.
     *
     * @return 2D array representing the current shape
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation state.
     *
     * @param currentShape the rotation index to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets a new brick and resets rotation to 0.
     *
     * @param brick the brick to set
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     * Gets the current brick object.
     *
     * @return the current brick
     */
    public Brick getCurrentBrick() {
        return brick;
    }
}
