package com.comp2042.model;

import java.util.List;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    List<int[][]> getNextQueueShapes();

    ViewData holdBrick();

    void resetHold();

    int[][] getHeldBrickShape();

    int hardDrop();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();
}
