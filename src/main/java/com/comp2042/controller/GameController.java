package com.comp2042.controller;

import com.comp2042.model.*;
import com.comp2042.service.ScoreService;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

            viewGuiController.updateNextQueue(board.getNextQueueShapes());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent() {
        ViewData data = board.holdBrick();
        viewGuiController.updateHold(board.getHeldBrickShape());
        return data;
    }

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

    @Override
    public void createNewGame() {
        board.resetHold();
        board.newGame();
        viewGuiController.updateNextQueue(board.getNextQueueShapes());
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
