package com.comp2042.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;


/**
 * Panel displayed when the game is over.
 * Shows a "GAME OVER" message.
 */
public class GameOverPanel extends BorderPane {

    /**
     * Creates a new game over panel.
     */
    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }
}