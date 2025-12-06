package com.comp2042.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Score model.
 * Tests score tracking and property binding functionality.
 */
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    void testInitialScore_IsZero() {
        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    void testAdd_IncreasesScore() {
        score.add(100);
        assertEquals(100, score.scoreProperty().get());
    }

    @Test
    void testAdd_MultipleAdditions() {
        score.add(50);
        score.add(30);
        score.add(20);

        assertEquals(100, score.scoreProperty().get());
    }

    @Test
    void testAdd_NegativeValue_DecreasesScore() {
        score.add(100);
        score.add(-30);

        assertEquals(70, score.scoreProperty().get());
    }

    @Test
    void testReset_SetsScoreToZero() {
        score.add(500);
        score.reset();

        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    void testScoreProperty_IsObservable() {
        final int[] observedValue = {0};

        score.scoreProperty().addListener((obs, oldVal, newVal) -> {
            observedValue[0] = newVal.intValue();
        });

        score.add(250);

        assertEquals(250, observedValue[0]);
    }

    @Test
    void testMultipleResets_WorkCorrectly() {
        score.add(100);
        score.reset();
        score.add(200);
        score.reset();

        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    void testAdd_Zero_DoesNotChangeScore() {
        score.add(100);
        score.add(0);

        assertEquals(100, score.scoreProperty().get());
    }
}
