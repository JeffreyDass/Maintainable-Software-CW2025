package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the player's score.
 * Uses JavaFX properties to allow UI binding.
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Gets the score property for UI binding.
     *
     * @return the score property
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds points to the current score.
     *
     * @param i the number of points to add
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score to zero.
     */
    public void reset() {
        score.setValue(0);
    }
}