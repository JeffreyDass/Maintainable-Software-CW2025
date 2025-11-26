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
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML private GridPane gamePanel;
    @FXML private GridPane brickPanel;
    @FXML private Group groupNotification;

    @FXML private GridPane nextPanel1;
    @FXML private GridPane nextPanel2;
    @FXML private GridPane nextPanel3;

    @FXML private GridPane holdPanel;

    @FXML private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;

    // 4×4 preview grids
    private final Rectangle[][] nextMatrix1 = new Rectangle[4][4];
    private final Rectangle[][] nextMatrix2 = new Rectangle[4][4];
    private final Rectangle[][] nextMatrix3 = new Rectangle[4][4];

    private final Rectangle[][] holdMatrix = new Rectangle[4][4];

    private InputEventListener eventListener;
    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader()
                .getResource("digital.ttf").toExternalForm(), 38);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        gamePanel.setOnKeyPressed(this::handleKeyPress);

        setupPreviewPanel(nextPanel1, nextMatrix1);
        setupPreviewPanel(nextPanel2, nextMatrix2);
        setupPreviewPanel(nextPanel3, nextMatrix3);

        setupPreviewPanel(holdPanel, holdMatrix);

        gameOverPanel.setVisible(false);
    }

    private void handleKeyPress(KeyEvent e) {
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

        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE - 42);

        timeLine = new Timeline(new KeyFrame(Duration.millis(400),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    /** Paint the falling brick */
    private void refreshBrick(ViewData brick) {
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE - 42);

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
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

            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notify =
                        new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notify);
                notify.showScore(groupNotification.getChildren());
            }

            refreshBrick(downData.getViewData());
        }

        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {}

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.set(true);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        timeLine.play();
        isPause.set(false);
        isGameOver.set(false);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
