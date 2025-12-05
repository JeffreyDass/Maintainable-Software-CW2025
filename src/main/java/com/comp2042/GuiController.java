package com.comp2042;

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

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

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

    // 4×4 preview grids
    private final Rectangle[][] nextMatrix1 = new Rectangle[4][4];
    private final Rectangle[][] nextMatrix2 = new Rectangle[4][4];
    private final Rectangle[][] nextMatrix3 = new Rectangle[4][4];

    private final Rectangle[][] holdMatrix = new Rectangle[4][4];

    private InputEventListener eventListener;
    private Timeline timeLine;
    private int totalLinesCleared = 0;
    private int level = 1;

    private static final int LINES_PER_LEVEL = 10;

    private MediaPlayer bgPlayer;
    private MediaPlayer musicPlayer;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader()
                .getResource("digital.ttf").toExternalForm(), 38);

        initBackgroundVideo();
        initBackgroundMusic();

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        gamePanel.setOnKeyPressed(this::handleKeyPress);

        setupPreviewPanel(nextPanel1, nextMatrix1);
        setupPreviewPanel(nextPanel2, nextMatrix2);
        setupPreviewPanel(nextPanel3, nextMatrix3);

        setupPreviewPanel(holdPanel, holdMatrix);

        btnResume.setOnAction(e -> resumeGame());
        btnNewGame.setOnAction(e -> newGame(null));
        btnQuit.setOnAction(e -> Platform.exit());

        gameOverPanel.setVisible(false);
    }

    private void initBackgroundVideo() {
        URL mediaUrl = getClass().getResource("/Tetris_Video_Background.mp4");

        Media media = new Media(mediaUrl.toExternalForm());
        bgPlayer = new MediaPlayer(media);

        bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgPlayer.setMute(true);

        backgroundVideo.setMediaPlayer(bgPlayer);

        bgPlayer.play();
    }

    private void initBackgroundMusic() {
        try {
            URL musicURL = getClass().getResource("/Tetris_BGM.mp3");
            if (musicURL == null) {
                System.out.println("Music file not found!");
                return;
            }

            Media music = new Media(musicURL.toExternalForm());
            musicPlayer = new MediaPlayer(music);

            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.1);

            musicPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleKeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            togglePause();
            return;
        }

        if (!isPause.get() && !isGameOver.get()) {

            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A)
                refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));

            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D)
                refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));

            if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W)
                refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));

            if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S)
                moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));

            if (e.getCode() == KeyCode.C) {
                ViewData data = eventListener.onHoldEvent();
                refreshBrick(data);
            }
            if (e.getCode() == KeyCode.SPACE) {
                DownData data = eventListener.onHardDropEvent();
                handleClearRow(data.getClearRow());
                refreshBrick(data.getViewData());
            }
        }

        if (e.getCode() == KeyCode.N)
            newGame(null);
    }

    /** Build a 4×4 preview grid inside a panel */
    private void setupPreviewPanel(GridPane panel, Rectangle[][] matrix) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setArcWidth(9);
                r.setArcHeight(9);
                r.setFill(Color.TRANSPARENT);

                matrix[i][j] = r;
                panel.add(r, j, i);
            }
        }
    }

    /** Initialize the game area */
    public void initGameView(int[][] boardMatrix, ViewData brick) {

        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = r;
                gamePanel.add(r, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < rectangles.length; i++) {
            for (int j = 0; j < rectangles[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = r;
                brickPanel.add(r, j, i);
            }
        }

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < rectangles.length; i++) {
            for (int j = 0; j < rectangles[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(getFillColor(brick.getBrickData()[i][j]));
                ghostRectangles[i][j] = r;
                ghostBrickPanel.add(r, j, i);
            }
        }

        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE + 130);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE - 42);

        ghostBrickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getGhostxPosition() * ghostBrickPanel.getVgap() + brick.getGhostxPosition() * BRICK_SIZE + 130);
        ghostBrickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getGhostyPosition() * ghostBrickPanel.getHgap() + brick.getGhostyPosition() * BRICK_SIZE - 42);

        timeLine = new Timeline(new KeyFrame(Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /** Paint the falling brick */
    public void refreshBrick(ViewData brick) {
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE + 130);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE - 42);

        ghostBrickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getGhostxPosition() * ghostBrickPanel.getVgap() + brick.getGhostxPosition() * BRICK_SIZE + 130);
        ghostBrickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getGhostyPosition() * ghostBrickPanel.getHgap() + brick.getGhostyPosition() * BRICK_SIZE - 42);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
            }
        }

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], ghostRectangles[i][j]);
            }
        }
    }

    /** Paint the background grid */
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /** Update the preview queue */
    public void updateNextQueue(List<int[][]> nextShapes) {
        if (nextShapes == null || nextShapes.isEmpty()) return;

        drawToPreview(nextShapes.get(0), nextMatrix1);
        if (nextShapes.size() > 1) drawToPreview(nextShapes.get(1), nextMatrix2);
        if (nextShapes.size() > 2) drawToPreview(nextShapes.get(2), nextMatrix3);
    }

    public void updateHold(int[][] shape) {
        clearPreview(holdMatrix);
        if (shape == null) return;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                setRectangleData(shape[r][c], holdMatrix[r][c]);
            }
        }
    }

    /** Draw a shape into a preview grid */
    private void drawToPreview(int[][] data, Rectangle[][] targetMatrix) {
        clearPreview(targetMatrix);

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                setRectangleData(data[i][j], targetMatrix[i][j]);
            }
        }
    }

    /** Clear a preview grid */
    private void clearPreview(Rectangle[][] matrix) {
        for (Rectangle[] row : matrix)
            for (Rectangle cell : row)
                cell.setFill(Color.TRANSPARENT);
    }

    private void setRectangleData(int color, Rectangle rect) {
        rect.setFill(getFillColor(color));
        rect.setArcWidth(9);
        rect.setArcHeight(9);
    }

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

    /** Called every tick */
    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData downData = eventListener.onDownEvent(event);
            handleClearRow(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }

        gamePanel.requestFocus();
    }

    private void handleClearRow(ClearRow clearRow) {
        if (clearRow == null || clearRow.getLinesRemoved() == 0) {
            return;
        }

        NotificationPanel notify =
                new NotificationPanel("+" + clearRow.getScoreBonus());
        groupNotification.getChildren().add(notify);
        notify.showScore(groupNotification.getChildren());

        totalLinesCleared += clearRow.getLinesRemoved();
        linesLabel.setText("Lines: " + totalLinesCleared);

        int newLevel = 1 + (totalLinesCleared / LINES_PER_LEVEL);
        if (newLevel != level) {
            level = newLevel;
            levelLabel.setText("Level: " + level);

            if (timeLine != null) {
                double speedFactor = 1.0 + (level - 1) * 0.25; // tweak as you like
                timeLine.setRate(speedFactor);
            }
        }
    }

    public void addLinesCleared(int lines) {
        totalLinesCleared += lines;
        linesLabel.setText("Lines: " + totalLinesCleared);
    }

    public void showScorePopup(int amount) {
        NotificationPanel notify = new NotificationPanel("+" + amount);
        groupNotification.getChildren().add(notify);
        notify.showScore(groupNotification.getChildren());
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty scoreProperty) {
        scoreProperty.addListener((obs, oldVal, newVal) -> {
            scoreLabel.setText("Score: " + newVal);
        });
    }

    public void togglePause() {
        if (isPause.get()) {
            resumeGame();
        } else {
            pauseGameOverlay();
        }
    }

    private void pauseGameOverlay() {
        isPause.set(true);
        timeLine.pause();
        pauseOverlay.setVisible(true);

        pauseOverlay.toFront();
        pauseOverlay.setMouseTransparent(false);
    }

    private void resumeGame() {
        pauseOverlay.setVisible(false);
        pauseOverlay.setMouseTransparent(true);
        pauseOverlay.toBack();

        isPause.set(false);
        timeLine.play();

        gamePanel.requestFocus();
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.set(true);
    }

    public void newGame(ActionEvent actionEvent) {

        pauseOverlay.setVisible(false);  // <-- FIX
        pauseOverlay.toBack();
        pauseOverlay.setMouseTransparent(true);

        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();

        totalLinesCleared = 0;
        level = 1;

        linesLabel.setText("Lines: 0");
        levelLabel.setText("Level: 1");

        if (timeLine != null) {
            timeLine.setRate(1.0);
        }

        timeLine.play();
        isPause.set(false);
        isGameOver.set(false);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
