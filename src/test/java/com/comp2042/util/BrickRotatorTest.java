package com.comp2042.util;

import com.comp2042.model.NextShapeInfo;
import com.comp2042.model.bricks.Brick;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for BrickRotator.
 * Tests brick rotation logic and shape management.
 */
class BrickRotatorTest {

    private BrickRotator rotator;
    private TestBrick testBrick;

    // Simple test brick with 2 rotation states
    private static class TestBrick implements Brick {
        private final List<int[][]> shapes = new ArrayList<>();

        public TestBrick() {
            shapes.add(new int[][]{{1, 1}, {0, 0}});
            shapes.add(new int[][]{{1, 0}, {1, 0}});
        }

        @Override
        public List<int[][]> getShapeMatrix() {
            return shapes;
        }
    }

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        testBrick = new TestBrick();
        rotator.setBrick(testBrick);
    }

    @Test
    void testSetBrick_ResetsCurrentShape() {
        rotator.setCurrentShape(1);
        rotator.setBrick(testBrick);

        // Should reset to 0
        int[][] shape = rotator.getCurrentShape();
        assertArrayEquals(new int[]{1, 1}, shape[0]);
    }

    @Test
    void testGetCurrentShape_ReturnsCorrectShape() {
        int[][] shape = rotator.getCurrentShape();

        assertNotNull(shape);
        assertEquals(2, shape.length);
        assertArrayEquals(new int[]{1, 1}, shape[0]);
    }

    @Test
    void testGetNextShape_ReturnsNextRotation() {
        NextShapeInfo nextInfo = rotator.getNextShape();

        assertEquals(1, nextInfo.getPosition());
        int[][] shape = nextInfo.getShape();
        assertArrayEquals(new int[]{1, 0}, shape[0]);
    }

    @Test
    void testGetNextShape_WrapsAround() {
        rotator.setCurrentShape(1); // Last position
        NextShapeInfo nextInfo = rotator.getNextShape();

        // Should wrap back to 0
        assertEquals(0, nextInfo.getPosition());
    }

    @Test
    void testSetCurrentShape_UpdatesPosition() {
        rotator.setCurrentShape(1);

        int[][] shape = rotator.getCurrentShape();
        assertArrayEquals(new int[]{1, 0}, shape[0]);
    }

    @Test
    void testGetCurrentBrick_ReturnsSameBrick() {
        Brick retrievedBrick = rotator.getCurrentBrick();
        assertSame(testBrick, retrievedBrick);
    }
}
