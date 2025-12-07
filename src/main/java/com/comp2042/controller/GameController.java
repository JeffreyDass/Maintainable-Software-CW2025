package com.comp2042.controller;

import com.comp2042.model.*;
import com.comp2042.service.ScoreService;

/**
 * Main game controller that handles game logic and user input.
 * Acts as a bridge between the game model (Board) and view (GuiController).
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);
    private final GuiController viewGuiController;

    /**
     * Creates a new game controller and initializes the game.
     *
     * @param c the GUI controller that handles the display
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
    }

    /**
     * Handles downward movement of the falling brick.
     * If the brick cannot move down, it merges with the board and a new brick is created.
     *
     * @param event the movement event containing source information
     * @return DownData containing cleared row info and updated view data
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        // Try to move brick down
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            // Brick has landed, merge it to the background
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            // Add score bonus if lines were cleared
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }

            // Create new brick and check for game over
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            viewGuiController.updateNextQueue(board.getNextQueueShapes());

        } else {
            // Award 1 point for manual down movement
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles left movement of the brick.
     *
     * @param event the movement event
     * @return updated view data for rendering
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        return board.getViewData();
    }

    /**
     * Handles right movement of the brick.
     *
     * @param event the movement event
     * @return updated view data for rendering
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        return board.getViewData();
    }

    /**
     * Handles rotation of the brick.
     *
     * @param event the movement event
     * @return updated view data for rendering
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        return board.getViewData();
    }

    /**
     * Handles the hold action where player swaps current brick with held brick.
     *
     * @return updated view data for rendering
     */
    @Override
    public ViewData onHoldEvent() {
        ViewData data = board.holdBrick();
        viewGuiController.updateHold(board.getHeldBrickShape());
        return data;
    }

    /**
     * Handles hard drop action where brick instantly drops to the bottom.
     * Awards bonus points based on distance dropped.
     *
     * @return DownData containing cleared row info and updated view data
     */
    @Override
    public DownData onHardDropEvent() {
        int rowsDropped = board.hardDrop();
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0)
            board.getScore().add(ScoreService.scoreForHardDrop(rowsDropped));
        viewGuiController.addLinesCleared(clearRow.getLinesRemoved());

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.updateNextQueue(board.getNextQueueShapes());

        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Resets the game state and starts a new game.
     */
    @Override
    public void createNewGame() {
        board.resetHold();
        board.newGame();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}