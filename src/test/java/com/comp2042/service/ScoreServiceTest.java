package com.comp2042.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ScoreService.
 * Tests hard drop scoring calculations.
 */
class ScoreServiceTest {

    @Test
    void testScoreForHardDrop_ZeroRows() {
        int score = ScoreService.scoreForHardDrop(0);
        assertEquals(0, score, "Score should be 0 for dropping 0 rows");
    }

    @Test
    void testScoreForHardDrop_OneRow() {
        int score = ScoreService.scoreForHardDrop(1);
        assertEquals(1, score, "Score should be 1 for dropping 1 row");
    }

    @Test
    void testScoreForHardDrop_MultipleRows() {
        int score = ScoreService.scoreForHardDrop(10);
        assertEquals(10, score, "Score should equal number of rows dropped");
    }

    @Test
    void testScoreForHardDrop_FiveRows() {
        int score = ScoreService.scoreForHardDrop(5);
        assertEquals(5, score, "Score should be 5 for dropping 5 rows");
    }

    @Test
    void testScoreForHardDrop_MaximumDrop() {
        int score = ScoreService.scoreForHardDrop(20);
        assertEquals(20, score, "Score should be 20 for maximum drop distance");
    }

    @Test
    void testScoreForHardDrop_LargeDrop() {
        int score = ScoreService.scoreForHardDrop(23);
        assertEquals(23, score, "Score should handle large drop distances");
    }

    @Test
    void testScoreForHardDrop_SmallDrop() {
        int score = ScoreService.scoreForHardDrop(2);
        assertEquals(2, score, "Score should be 2 for dropping 2 rows");
    }
}
