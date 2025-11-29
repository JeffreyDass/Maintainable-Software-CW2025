package com.comp2042;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostxPosition;
    private final int ghostyPosition;
    private final int[][] nextBrickData;

    public ViewData(int[][] brickData, int xPosition, int yPosition, int ghostxPosition, int ghostyPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostxPosition = ghostxPosition;
        this.ghostyPosition = ghostyPosition;
        this.nextBrickData = nextBrickData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getGhostxPosition() {
        return ghostxPosition;
    }

    public int getGhostyPosition() { return ghostyPosition; }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
