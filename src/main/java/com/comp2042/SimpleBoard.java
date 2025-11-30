package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.Point;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private Point ghostBrickOffset;
    private final Score score;
    private Brick heldBrick = null;
    private boolean holdUsed = false;
    private final Queue<Brick> nextPieces = new ArrayDeque<>();

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        for (int i = 0; i < 3; i++) {
            nextPieces.add(brickGenerator.getBrick());
        }
    }

    public Brick getHeldBrick() {
        return heldBrick;
    }


    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            updateGhostBrick();
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            updateGhostBrick();
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            updateGhostBrick();
            return true;
        }
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            updateGhostBrick();
            return true;
        }
    }

    public ViewData holdBrick() {

        // If player already held this turn, ignore
        if (holdUsed) {
            return getViewData();
        }

        if (heldBrick == null) {
            // First time holding: store current brick, spawn a new one
            heldBrick = brickRotator.getCurrentBrick();
            createNewBrick();
        } else {
            // Swap held ↔ current
            Brick temp = brickRotator.getCurrentBrick();
            brickRotator.setBrick(heldBrick);
            heldBrick = temp;

            // Reset spawn offset after swap
            currentOffset = new Point(4, 1);
        }

        holdUsed = true;
        return getViewData();
    }

    @Override
    public int hardDrop() {
        int rowsDropped = ghostBrickOffset.y - currentOffset.y;

        currentOffset = new Point(ghostBrickOffset);

        updateGhostBrick();

        return rowsDropped;
    }


    private boolean updateGhostBrick() {
        Point p = calculateGhostBrickOffset(currentOffset);
        ghostBrickOffset = p;
        return true;
    }

    private Point calculateGhostBrickOffset(Point num) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(num);

        while(true) {
            p.setLocation(currentOffset.getX(), p.getY() + 1);
            boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
            if (conflict) {
                p.setLocation(p.getX(), p.getY() - 1) ;
                return p;
            }

        }
    }

    @Override
    public boolean createNewBrick() {

        // Take the next brick from the queue
        Brick currentBrick = nextPieces.poll();
        brickRotator.setBrick(currentBrick);

        // Generate a new brick and add to queue
        nextPieces.add(brickGenerator.getBrick());

        // Reset hold usage for this turn
        holdUsed = false;

        // Spawn position (centered)
        currentOffset = new Point(4, 1);

        ghostBrickOffset = calculateGhostBrickOffset(currentOffset);

        // Check game-over on spawn
        return MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y
        );
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {

        // Get the NEXT brick's shape from queue (without removing it)
        Brick next = nextPieces.peek();
        int[][] nextShape = next.getShapeMatrix().get(0);

        return new ViewData(
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y,
                ghostBrickOffset.x,
                ghostBrickOffset.y,
                nextShape
        );
    }

    @Override
    public List<int[][]> getNextQueueShapes() {
        List<int[][]> list = new ArrayList<>();
        for (Brick b : nextPieces) {
            list.add(b.getShapeMatrix().get(0));
        }
        return list;
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix,
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY());

        holdUsed = false;   // ⭐ reset HOLD lock AFTER brick lands
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public int[][] getHeldBrickShape() {
        if (heldBrick == null) return null;
        return heldBrick.getShapeMatrix().get(0);
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}
