package com.comp2042.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LevelService.
 * Tests level calculation based on lines cleared.
 */
class LevelServiceTest {

    @Test
    void testCalculateLevel_ZeroLines_ReturnsLevelOne() {
        int level = LevelService.calculateLevel(0, 10);
        assertEquals(1, level, "Level should be 1 with 0 lines cleared");
    }

    @Test
    void testCalculateLevel_LessThanThreshold_ReturnsLevelOne() {
        int level = LevelService.calculateLevel(5, 10);
        assertEquals(1, level, "Level should remain 1 when lines < threshold");
    }

    @Test
    void testCalculateLevel_NineLines_StillLevelOne() {
        int level = LevelService.calculateLevel(9, 10);
        assertEquals(1, level, "Level should be 1 with 9 lines (just before threshold)");
    }

    @Test
    void testCalculateLevel_ExactlyThreshold_ReturnsLevelTwo() {
        int level = LevelService.calculateLevel(10, 10);
        assertEquals(2, level, "Level should be 2 when exactly 10 lines cleared");
    }

    @Test
    void testCalculateLevel_TwentyLines_ReturnsLevelThree() {
        int level = LevelService.calculateLevel(20, 10);
        assertEquals(3, level, "Level should be 3 with 20 lines cleared");
    }

    @Test
    void testCalculateLevel_TwentyFiveLines_ReturnsLevelThree() {
        int level = LevelService.calculateLevel(25, 10);
        assertEquals(3, level, "Level should be 3 with 25 lines (between thresholds)");
    }

    @Test
    void testCalculateLevel_CustomThreshold_FiveLines() {
        int level = LevelService.calculateLevel(15, 5);
        assertEquals(4, level, "Level should be 4: 1 + (15/5) = 1 + 3 = 4");
    }

    @Test
    void testCalculateLevel_CustomThreshold_TwentyLines() {
        int level = LevelService.calculateLevel(100, 20);
        assertEquals(6, level, "Level should be 6: 1 + (100/20) = 1 + 5 = 6");
    }

    @Test
    void testCalculateLevel_LargeNumbers() {
        int level = LevelService.calculateLevel(1000, 10);
        assertEquals(101, level, "Level should be 101: 1 + (1000/10) = 1 + 100 = 101");
    }

    @Test
    void testCalculateLevel_OneHundredLines_DefaultThreshold() {
        int level = LevelService.calculateLevel(100, 10);
        assertEquals(11, level, "Level should be 11 with 100 lines cleared");
    }
}
