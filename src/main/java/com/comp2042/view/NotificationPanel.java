package com.comp2042.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Animated panel that displays score bonuses.
 * Fades out and floats upward when shown.
 */
public class NotificationPanel extends BorderPane {

    /**
     * Creates a new notification panel with text.
     *
     * @param text the text to display (usually a score bonus)
     */
    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);
    }

    /**
     * Animates the notification with fade and float effects.
     * Removes itself from the scene after animation completes.
     *
     * @param list the list of nodes to remove this panel from
     */
    public void showScore(ObservableList<Node> list) {
        // Create fade out animation
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);

        // Create upward movement animation
        TranslateTransition tt = new TranslateTransition(Duration.millis(2500), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);

        // Play both animations together
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}
