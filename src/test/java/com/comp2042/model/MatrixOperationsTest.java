package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MatrixOperations utility methods.
 * Tests matrix manipulation, collision detection, and row clearing logic.
 */
class MatrixOperationsTest {

    private int[][] testMatrix;
    private int[][] testBrick;

    @BeforeEach
    void setUp() {
        // Create a simple 10x10 test matrix
        testMatrix = new int[10][10];

        // Create a simple 2x2 brick
        testBrick = new int[][]{
                {1, 1},
                {1, 1}
        };
    }

    @Test
    void testCopy_CreatesIndependentCopy() {
        int[][] original = new int[][]{{1, 2}, {3, 4}};
        int[][] copy = MatrixOperations.copy(original);

        // Modify the copy
        copy[0][0] = 999;

        // Original should remain unchanged
        assertEquals(1, original[0][0]);
        assertEquals(999, copy[0][0]);
    }

    @Test
    void testIntersect_NoCollision_ReturnsFalse() {
        // Empty matrix, brick at position (0,0) should not collide
        boolean result = MatrixOperations.intersect(testMatrix, testBrick, 0, 0);
        assertFalse(result, "Brick should not intersect with empty space");
    }

    @Test
    void testIntersect_WithOccupiedSpace_ReturnsTrue() {
        // Place something in the matrix
        testMatrix[0][0] = 1;

        // Brick at (0,0) should now collide
        boolean result = MatrixOperations.intersect(testMatrix, testBrick, 0, 0);
        assertTrue(result, "Brick should intersect with occupied space");
    }

    @Test
    void testIntersect_OutOfBoundsLeft_ReturnsTrue() {
        // Try to place brick at negative x position
        boolean result = MatrixOperations.intersect(testMatrix, testBrick, -1, 0);
        assertTrue(result, "Brick should intersect when out of bounds (left)");
    }

    @Test
    void testIntersect_OutOfBoundsRight_ReturnsTrue() {
        // Try to place brick beyond right boundary
        boolean result = MatrixOperations.intersect(testMatrix, testBrick, 9, 0);
        assertTrue(result, "Brick should intersect when out of bounds (right)");
    }

    @Test
    void testIntersect_OutOfBoundsBottom_ReturnsTrue() {
        // Try to place brick beyond bottom boundary
        boolean result = MatrixOperations.intersect(testMatrix, testBrick, 0, 9);
        assertTrue(result, "Brick should intersect when out of bounds (bottom)");
    }

    @Test
    void testMerge_BrickAddedToMatrix() {
        int[][] result = MatrixOperations.merge(testMatrix, testBrick, 2, 2);

        // Check that brick was placed at correct position
        assertEquals(1, result[2][2]);
        assertEquals(1, result[2][3]);
        assertEquals(1, result[3][2]);
        assertEquals(1, result[3][3]);

        // Check that original position is empty
        assertEquals(0, result[0][0]);
    }

    @Test
    void testMerge_DoesNotModifyOriginal() {
        int[][] result = MatrixOperations.merge(testMatrix, testBrick, 2, 2);

        // Original matrix should remain unchanged
        assertEquals(0, testMatrix[2][2]);
        assertEquals(1, result[2][2]);
    }

    @Test
    void testCheckRemoving_NoFullRows_ReturnsZeroLines() {
        ClearRow result = MatrixOperations.checkRemoving(testMatrix);

        assertEquals(0, result.getLinesRemoved());
        assertEquals(0, result.getScoreBonus());
    }

    @Test
    void testCheckRemoving_OneFullRow_RemovesCorrectly() {
        // Fill the bottom row
        for (int j = 0; j < testMatrix[0].length; j++) {
            testMatrix[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(testMatrix);

        assertEquals(1, result.getLinesRemoved());
        assertEquals(50, result.getScoreBonus()); // 50 * 1 * 1

        // Bottom row should now be empty
        int[][] newMatrix = result.getNewMatrix();
        for (int j = 0; j < newMatrix[0].length; j++) {
            assertEquals(0, newMatrix[9][j]);
        }
    }

    @Test
    void testCheckRemoving_MultipleFullRows_CalculatesCorrectScore() {
        // Fill two rows
        for (int j = 0; j < testMatrix[0].length; j++) {
            testMatrix[8][j] = 1;
            testMatrix[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(testMatrix);

        assertEquals(2, result.getLinesRemoved());
        assertEquals(200, result.getScoreBonus()); // 50 * 2 * 2
    }

    @Test
    void testCheckRemoving_PartialRow_DoesNotClear() {
        // Fill bottom row except one cell
        for (int j = 0; j < testMatrix[0].length - 1; j++) {
            testMatrix[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(testMatrix);

        assertEquals(0, result.getLinesRemoved());
    }

    @Test
    void testCheckRemoving_RowsShiftDownCorrectly() {
        // Place a brick in row 7
        testMatrix[7][5] = 2;

        // Fill row 9 (will be removed)
        for (int j = 0; j < testMatrix[0].length; j++) {
            testMatrix[9][j] = 1;
        }

        ClearRow result = MatrixOperations.checkRemoving(testMatrix);
        int[][] newMatrix = result.getNewMatrix();

        // The brick from row 7 should have moved down to row 8
        // (because row 9 was removed and everything shifted down)
        assertEquals(2, newMatrix[8][5]);

        // Row 9 should be empty now (new empty row at top)
        assertEquals(0, newMatrix[9][5]);

        // Row 0 should be empty (rows shift down, empty rows added at top)
        assertEquals(0, newMatrix[0][5]);
    }

    @Test
    void testDeepCopyList_CreatesIndependentCopies() {
        java.util.List<int[][]> originalList = new java.util.ArrayList<>();
        originalList.add(new int[][]{{1, 2}, {3, 4}});

        java.util.List<int[][]> copiedList = MatrixOperations.deepCopyList(originalList);

        // Modify the copy
        copiedList.get(0)[0][0] = 999;

        // Original should remain unchanged
        assertEquals(1, originalList.get(0)[0][0]);
        assertEquals(999, copiedList.get(0)[0][0]);
    }
}
