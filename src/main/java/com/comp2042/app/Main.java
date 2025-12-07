package com.comp2042.app;

import com.comp2042.controller.GameController;
import com.comp2042.controller.GuiController;
import com.comp2042.config.GameConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main application class that starts the Tetris game.
 * This class extends JavaFX Application and sets up the game window.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application and sets up the game window.
     * Loads the FXML layout and initializes the game controller.
     *
     * @param primaryStage the main window of the application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setMaximized(false);
        primaryStage.setFullScreen(false);
        primaryStage.show();
        new GameController(c);
    }

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}