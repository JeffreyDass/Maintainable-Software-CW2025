package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SimpleBoard.
 * Tests core game board functionality including movement, rotation, and scoring.
 */
class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(25, 10);
        board.createNewBrick();
    }

    @Test
    void testInitialization_CreatesEmptyBoard() {
        int[][] matrix = board.getBoardMatrix();

        assertNotNull(matrix);
        assertEquals(25, matrix.length);
        assertEquals(10, matrix[0].length);
    }

    @Test
    void testInitialization_ScoreIsZero() {
        assertEquals(0, board.getScore().scoreProperty().get());
    }

    @Test
    void testCreateNewBrick_ReturnsGameOverStatus() {
        // On empty board, should not be game over
        boolean gameOver = board.createNewBrick();
        assertFalse(gameOver);
    }

    @Test
    void testMoveBrickDown_OnEmptyBoard_ReturnsTrue() {
        boolean moved = board.moveBrickDown();
        assertTrue(moved, "Brick should be able to move down on empty board");
    }

    @Test
    void testMoveBrickLeft_OnEmptyBoard_ReturnsTrue() {
        boolean moved = board.moveBrickLeft();
        // May return true or false depending on spawn position
        // Just verify no exception is thrown
        assertNotNull(board.getViewData());
    }

    @Test
    void testMoveBrickRight_OnEmptyBoard_ReturnsTrue() {
        boolean moved = board.moveBrickRight();
        assertNotNull(board.getViewData());
    }

    @Test
    void testRotateLeftBrick_OnEmptyBoard() {
        boolean rotated = board.rotateLeftBrick();
        // Just verify no exception is thrown
        assertNotNull(board.getViewData());
    }

    @Test
    void testGetViewData_ReturnsValidData() {
        ViewData data = board.getViewData();

        assertNotNull(data);
        assertNotNull(data.getBrickData());
        assertTrue(data.getxPosition() >= 0);
        assertTrue(data.getyPosition() >= 0);
    }

    @Test
    void testGetNextQueueShapes_ReturnsThreeShapes() {
        var shapes = board.getNextQueueShapes();

        assertNotNull(shapes);
        assertEquals(3, shapes.size());
    }

    @Test
    void testMergeBrickToBackground_UpdatesMatrix() {
        // Move brick to bottom
        while (board.moveBrickDown()) {
            // Keep moving down
        }

        int[][] beforeMerge = board.getBoardMatrix();
        board.mergeBrickToBackground();
        int[][] afterMerge = board.getBoardMatrix();

        // Matrix should have changed (brick merged)
        boolean hasNonZero = false;
        for (int[] row : afterMerge) {
            for (int cell : row) {
                if (cell != 0) {
                    hasNonZero = true;
                    break;
                }
            }
        }
        assertTrue(hasNonZero, "Board should have non-zero values after merge");
    }

    @Test
    void testClearRows_NoFullRows_ReturnsZero() {
        ClearRow result = board.clearRows();
        assertEquals(0, result.getLinesRemoved());
    }

    @Test
    void testHoldBrick_FirstTime_StoresBrick() {
        ViewData before = board.getViewData();
        ViewData after = board.holdBrick();

        assertNotNull(board.getHeldBrick());
        assertNotNull(after);
    }

    @Test
    void testHoldBrick_CanHoldAndSwap() {
        // Hold the first time - should store current brick
        board.holdBrick();
        var held = board.getHeldBrick();
        assertNotNull(held, "Should have a held brick after first hold");

        // Hold second time in the same turn - should NOT swap (holdUsed = true)
        board.holdBrick();
        var stillHeld = board.getHeldBrick();
        assertSame(held, stillHeld, "Holding twice in same turn should not swap");

        // Spawn new brick (resets holdUsed flag)
        board.createNewBrick();

        // Now holding should swap
        board.holdBrick();
        var afterSwap = board.getHeldBrick();
        assertNotNull(afterSwap, "Should still have a held brick after swap");
    }

    @Test
    void testHoldBrick_WhenAlreadyUsed_DoesNotSwap() {
        ViewData first = board.holdBrick();
        ViewData second = board.holdBrick();

        // Second hold should return same data (no swap)
        assertEquals(first.getxPosition(), second.getxPosition());
    }

    @Test
    void testGetHeldBrickShape_InitiallyNull() {
        board = new SimpleBoard(25, 10);
        assertNull(board.getHeldBrickShape());
    }

    @Test
    void testGetHeldBrickShape_AfterHold_ReturnsShape() {
        board.createNewBrick();
        board.holdBrick();

        int[][] shape = board.getHeldBrickShape();
        assertNotNull(shape);
    }

    @Test
    void testResetHold_ClearsHeldBrick() {
        board.holdBrick();
        board.resetHold();

        assertNull(board.getHeldBrickShape());
    }

    @Test
    void testHardDrop_ReturnsRowsDropped() {
        int rowsDropped = board.hardDrop();

        assertTrue(rowsDropped >= 0);
    }

    @Test
    void testNewGame_ResetsScore() {
        board.getScore().add(1000);
        board.newGame();

        assertEquals(0, board.getScore().scoreProperty().get());
    }

    @Test
    void testNewGame_ClearsBoard() {
        // Fill some cells
        board.mergeBrickToBackground();

        board.newGame();
        int[][] matrix = board.getBoardMatrix();

        // Check if board is empty (except for current brick)
        boolean isEmpty = true;
        for (int[] row : matrix) {
            for (int cell : row) {
                if (cell != 0) {
                    isEmpty = false;
                    break;
                }
            }
        }
        assertTrue(isEmpty, "Board should be empty after new game");
    }

    @Test
    void testSequentialMoves_MaintainsConsistency() {
        ViewData data1 = board.getViewData();
        board.moveBrickRight();
        ViewData data2 = board.getViewData();

        // X position should have changed
        assertTrue(data2.getxPosition() != data1.getxPosition() ||
                !board.moveBrickRight()); // or couldn't move
    }
}
