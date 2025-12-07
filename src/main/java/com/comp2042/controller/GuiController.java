package com.comp2042.controller;

import com.comp2042.media.BackgroundMusicService;
import com.comp2042.model.*;
import com.comp2042.view.GameOverPanel;
import com.comp2042.view.NotificationPanel;
import com.comp2042.config.GameConfig;
import com.comp2042.service.LevelService;
import com.comp2042.media.BackgroundMediaService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls the game's graphical user interface.
 * Handles rendering, animations, user input, and UI updates.
 * This is the main view controller that connects the UI to the game logic.
 */
public class GuiController implements Initializable {

    @FXML private GridPane gamePanel;
    @FXML private GridPane brickPanel;
    @FXML private GridPane ghostBrickPanel;
    @FXML private Group groupNotification;

    @FXML private GridPane nextPanel1;
    @FXML private GridPane nextPanel2;
    @FXML private GridPane nextPanel3;

    @FXML private GridPane holdPanel;

    @FXML private Label scoreLabel;
    @FXML private Label linesLabel;
    @FXML private Label levelLabel;

    @FXML private AnchorPane pauseOverlay;
    @FXML private Button btnResume;
    @FXML private Button btnNewGame;
    @FXML private Button btnQuit;

    @FXML private MediaView backgroundVideo;
    @FXML private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;

    // 4×4 preview grids for next pieces
    private final Rectangle[][] nextMatrix1 = new Rectangle[4][4];
    private final Rectangle[][] nextMatrix2 = new Rectangle[4][4];
    private final Rectangle[][] nextMatrix3 = new Rectangle[4][4];

    private final Rectangle[][] holdMatrix = new Rectangle[4][4];

    private InputEventListener eventListener;
    private Timeline timeLine;
    private int totalLinesCleared = 0;
    private int level = 1;

    private static MediaPlayer bgPlayer;
    private static MediaPlayer musicPlayer;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    /**
     * Initializes the GUI when the FXML is loaded.
     * Sets up fonts, media players, preview panels, and event handlers.
     *
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load custom font
        Font.loadFont(getClass().getClassLoader()
                .getResource("digital.ttf").toExternalForm(), 38);

        initBackgroundVideo();
        initBackgroundMusic();

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        // Set up keyboard input
        gamePanel.setOnKeyPressed(this::handleKeyPress);

        // Initialize preview panels
        setupPreviewPanel(nextPanel1, nextMatrix1);
        setupPreviewPanel(nextPanel2, nextMatrix2);
        setupPreviewPanel(nextPanel3, nextMatrix3);
        setupPreviewPanel(holdPanel, holdMatrix);

        // Set up pause menu buttons
        btnResume.setOnAction(e -> resumeGame());
        btnNewGame.setOnAction(e -> newGame(null));
        btnQuit.setOnAction(e -> Platform.exit());

        gameOverPanel.setVisible(false);
    }

    /**
     * Initializes the background video player.
     */
    private void initBackgroundVideo() {
        bgPlayer = BackgroundMediaService.createVideoPlayer(
                "/Tetris_Video_Background.mp4",
                0.0
        );

        backgroundVideo.setMediaPlayer(bgPlayer);
        bgPlayer.play();
    }

    /**
     * Initializes the background music player.
     */
    private void initBackgroundMusic() {
        musicPlayer = BackgroundMusicService.createBgmPlayer(
                "/Tetris_BGM.mp3",
                0.1
        );
        musicPlayer.play();
    }

    /**
     * Handles keyboard input from the player.
     * Maps keys to game actions like movement and rotation.
     *
     * @param e the key event
     */
    private void handleKeyPress(KeyEvent e) {
        // ESC toggles pause
        if (e.getCode() == KeyCode.ESCAPE) {
            togglePause();
            return;
        }

        // Only accept game input when not paused or game over
        if (!isPause.get() && !isGameOver.get()) {

            // Left movement (Left arrow or A)
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A)
                refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));

            // Right movement (Right arrow or D)
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D)
                refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));

            // Rotate (Up arrow or W)
            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W)
                refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));

            // Soft drop (Down arrow or S)
            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S)
                moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));

            // Hold piece (C key)
            if (e.getCode() == KeyCode.C) {
                ViewData data = eventListener.onHoldEvent();
                refreshBrick(data);
            }

            // Hard drop (Space bar)
            if (e.getCode() == KeyCode.SPACE) {
                DownData data = eventListener.onHardDropEvent();
                handleClearRow(data.getClearRow());
                refreshBrick(data.getViewData());
            }
        }

        // N key starts new game anytime
        if (e.getCode() == KeyCode.N)
            newGame(null);
    }

    /**
     * Builds a 4x4 preview grid inside a panel.
     * Used for showing next pieces and held piece.
     *
     * @param panel the GridPane to fill
     * @param matrix the Rectangle array to populate
     */
    private void setupPreviewPanel(GridPane panel, Rectangle[][] matrix) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle r = new Rectangle(GameConfig.BRICK_SIZE, GameConfig.BRICK_SIZE);
                r.setArcWidth(9);
                r.setArcHeight(9);
                r.setFill(Color.TRANSPARENT);

                matrix[i][j] = r;
                panel.add(r, j, i);
            }
        }
    }

    /**
     * Initializes the main game area with rectangles for rendering.
     * Creates the display matrix, brick panel, and ghost brick panel.
     * Starts the game timer for automatic brick dropping.
     *
     * @param boardMatrix the game board state
     * @param brick initial brick view data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {

        // Create rectangles for the board (skip first 2 rows - buffer zone)
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle r = new Rectangle(GameConfig.BRICK_SIZE, GameConfig.BRICK_SIZE);
                r.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = r;
                gamePanel.add(r, j, i - 2);
            }
        }

        // Create rectangles for the falling brick
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < rectangles.length; i++) {
            for (int j = 0; j < rectangles[i].length; j++) {
                Rectangle r = new Rectangle(GameConfig.BRICK_SIZE, GameConfig.BRICK_SIZE);
                r.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = r;
                brickPanel.add(r, j, i);
            }
        }

        // Create rectangles for the ghost brick
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < rectangles.length; i++) {
            for (int j = 0; j < rectangles[i].length; j++) {
                Rectangle r = new Rectangle(GameConfig.BRICK_SIZE, GameConfig.BRICK_SIZE);
                r.setFill(getFillColor(brick.getBrickData()[i][j]));
                ghostRectangles[i][j] = r;
                ghostBrickPanel.add(r, j, i);
            }
        }

        // Position the brick panels
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * GameConfig.BRICK_SIZE + 130);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * GameConfig.BRICK_SIZE - 42);

        ghostBrickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getGhostxPosition() * ghostBrickPanel.getVgap() + brick.getGhostxPosition() * GameConfig.BRICK_SIZE + 130);
        ghostBrickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getGhostyPosition() * ghostBrickPanel.getHgap() + brick.getGhostyPosition() * GameConfig.BRICK_SIZE - 42);

        // Start game timer (brick falls every 400ms)
        timeLine = new Timeline(new KeyFrame(Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /**
     * Updates the display of the falling brick and ghost brick.
     * Repositions the brick panels and updates colors.
     *
     * @param brick the view data containing positions and shapes
     */
    public void refreshBrick(ViewData brick) {
        // Update brick position
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * GameConfig.BRICK_SIZE + 130);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * GameConfig.BRICK_SIZE - 42);

        // Update ghost brick position
        ghostBrickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getGhostxPosition() * ghostBrickPanel.getVgap() + brick.getGhostxPosition() * GameConfig.BRICK_SIZE + 130);
        ghostBrickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getGhostyPosition() * ghostBrickPanel.getHgap() + brick.getGhostyPosition() * GameConfig.BRICK_SIZE - 42);

        // Update brick colors
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }

        // Update ghost brick with transparency
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {

                int colorId = brick.getBrickData()[i][j];
                Paint baseColor = getFillColor(colorId);

                // Make ghost piece 50% transparent
                Paint ghostColor = ((Color) baseColor).deriveColor(0, 1, 1, 0.5);

                ghostRectangles[i][j].setFill(ghostColor);
                ghostRectangles[i][j].setArcWidth(9);
                ghostRectangles[i][j].setArcHeight(9);
            }
        }
    }

    /**
     * Redraws the background board with placed bricks.
     *
     * @param board the current board matrix
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Updates the preview panels showing the next pieces.
     *
     * @param nextShapes list of upcoming brick shapes
     */
    public void updateNextQueue(List<int[][]> nextShapes) {
        if (nextShapes == null || nextShapes.isEmpty()) return;

        drawToPreview(nextShapes.get(0), nextMatrix1);
        if (nextShapes.size() > 1) drawToPreview(nextShapes.get(1), nextMatrix2);
        if (nextShapes.size() > 2) drawToPreview(nextShapes.get(2), nextMatrix3);
    }

    /**
     * Updates the hold panel showing the held piece.
     *
     * @param shape the held brick shape, or null if no piece is held
     */
    public void updateHold(int[][] shape) {
        clearPreview(holdMatrix);
        if (shape == null) return;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                setRectangleData(shape[r][c], holdMatrix[r][c]);
            }
        }
    }

    /**
     * Draws a brick shape into a preview grid.
     *
     * @param data the brick shape matrix
     * @param targetMatrix the preview grid to draw into
     */
    private void drawToPreview(int[][] data, Rectangle[][] targetMatrix) {
        clearPreview(targetMatrix);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                setRectangleData(data[i][j], targetMatrix[i][j]);
            }
        }
    }

    /**
     * Clears a preview grid by making all cells transparent.
     *
     * @param matrix the preview grid to clear
     */
    private void clearPreview(Rectangle[][] matrix) {
        for (Rectangle[] row : matrix)
            for (Rectangle cell : row)
                cell.setFill(Color.TRANSPARENT);
    }

    /**
     * Sets the color and appearance of a rectangle.
     *
     * @param color the color ID
     * @param rect the rectangle to update
     */
    private void setRectangleData(int color, Rectangle rect) {
        rect.setFill(getFillColor(color));
        rect.setArcWidth(9);
        rect.setArcHeight(9);
    }

    /**
     * Maps a color ID to a JavaFX Color.
     *
     * @param i the color ID
     * @return the corresponding Color
     */
    private Paint getFillColor(int i) {
        return switch (i) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.AQUA;
            case 2 -> Color.BLUEVIOLET;
            case 3 -> Color.DARKGREEN;
            case 4 -> Color.YELLOW;
            case 5 -> Color.RED;
            case 6 -> Color.BEIGE;
            case 7 -> Color.BURLYWOOD;
            default -> Color.WHITE;
        };
    }

    /**
     * Called every game tick to move the brick down.
     *
     * @param event the down movement event
     */
    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);
            handleClearRow(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }

        gamePanel.requestFocus();
    }

    /**
     * Handles clearing rows and updating level/speed.
     * Shows score popup and checks for level progression.
     *
     * @param clearRow information about cleared rows
     */
    private void handleClearRow(ClearRow clearRow) {
        if (clearRow == null || clearRow.getLinesRemoved() == 0) {
            return;
        }

        // Show score bonus popup
        NotificationPanel notify =
                new NotificationPanel("+" + clearRow.getScoreBonus());
        groupNotification.getChildren().add(notify);
        notify.showScore(groupNotification.getChildren());

        // Update lines count
        totalLinesCleared += clearRow.getLinesRemoved();
        linesLabel.setText("Lines: " + totalLinesCleared);

        // Check for level up
        int newLevel = LevelService.calculateLevel(
                totalLinesCleared,
                GameConfig.LINES_PER_LEVEL
        );
        if (newLevel != level) {
            level = newLevel;
            levelLabel.setText("Level: " + level);

            // Increase game speed
            if (timeLine != null) {
                double speedFactor = 1.0 + (level - 1) * 0.25;
                timeLine.setRate(speedFactor);
            }
        }
    }

    /**
     * Adds to the lines cleared count.
     *
     * @param lines number of lines to add
     */
    public void addLinesCleared(int lines) {
        totalLinesCleared += lines;
        linesLabel.setText("Lines: " + totalLinesCleared);
    }

    /**
     * Shows a score popup animation.
     *
     * @param amount the score amount to display
     */
    public void showScorePopup(int amount) {
        NotificationPanel notify = new NotificationPanel("+" + amount);
        groupNotification.getChildren().add(notify);
        notify.showScore(groupNotification.getChildren());
    }

    /**
     * Sets the event listener for game input.
     *
     * @param eventListener the listener to handle input events
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score label to a score property.
     * Updates automatically when score changes.
     *
     * @param scoreProperty the score property to bind to
     */
    public void bindScore(IntegerProperty scoreProperty) {
        scoreProperty.addListener((obs, oldVal, newVal) -> {
            scoreLabel.setText("Score: " + newVal);
        });
    }

    /**
     * Toggles between pause and resume.
     */
    public void togglePause() {
        if (isPause.get()) {
            resumeGame();
        } else {
            pauseGameOverlay();
        }
    }

    /**
     * Pauses the game and shows pause menu.
     */
    private void pauseGameOverlay() {
        isPause.set(true);
        timeLine.pause();

        if (bgPlayer != null) bgPlayer.pause();
        if (musicPlayer != null) musicPlayer.pause();

        pauseOverlay.setVisible(true);
        pauseOverlay.toFront();
        pauseOverlay.setMouseTransparent(false);
    }

    /**
     * Resumes the game from pause.
     */
    private void resumeGame() {
        pauseOverlay.setVisible(false);
        pauseOverlay.setMouseTransparent(true);
        pauseOverlay.toBack();

        isPause.set(false);
        timeLine.play();

        if (!isGameOver.get()) {
            if (bgPlayer != null) bgPlayer.play();
            if (musicPlayer != null) musicPlayer.play();
        }

        gamePanel.requestFocus();
    }

    /**
     * Handles game over state.
     * Stops the game and shows game over screen.
     */
    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.set(true);

        if (bgPlayer != null) {
            bgPlayer.pause();
            bgPlayer.seek(Duration.ZERO);
        }
        if (musicPlayer != null) {
            musicPlayer.pause();
            musicPlayer.seek(Duration.ZERO);
        }
    }

    /**
     * Starts a new game.
     * Resets all game state and restarts music.
     *
     * @param actionEvent not used
     */
    public void newGame(ActionEvent actionEvent) {
        pauseOverlay.setVisible(false);
        pauseOverlay.toBack();
        pauseOverlay.setMouseTransparent(true);

        timeLine.stop();
        gameOverPanel.setVisible(false);

        eventListener.createNewGame();

        // Reset game stats
        totalLinesCleared = 0;
        level = 1;
        linesLabel.setText("Lines: 0");
        levelLabel.setText("Level: 1");

        if (timeLine != null) {
            timeLine.setRate(1.0);
        }

        updateHold(null);

        isPause.set(false);
        isGameOver.set(false);

        timeLine.play();

        // Restart media
        if (bgPlayer != null) {
            bgPlayer.seek(Duration.ZERO);
            bgPlayer.play();
        }
        if (musicPlayer != null) {
            musicPlayer.seek(Duration.ZERO);
            musicPlayer.play();
        }

        gamePanel.requestFocus();
    }

    /**
     * Handles pause button action.
     *
     * @param actionEvent not used
     */
    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
