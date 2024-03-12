package ubc.cosc322.core;

import java.util.ArrayList;
import java.util.List;

public class Board {
    int[][] boardValues;
    public static final int DEFAULT_BOARD_SIZE = 10;
    public static final int IN_PROGRESS = -1;
    public static final int DRAW = 0;
    public static final int P1 = 1;
    public static final int P2 = 2;

    public Board() {
        this.boardValues = new int[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE]; // 10 x 10 board
    }

    public List<Position> getLegalMoves(int x, int y) {
        List<Position> legalMoves = new ArrayList<>();

        // Directions the queen can move, ordered as N, E, S, W, NE, SE, SW, NW
        int[] directionsX = {0, 1, 0, -1, 1, 1, -1, -1}; // X changes for E, W, and diagonals
        int[] directionsY = {1, 0, -1, 0, 1, -1, -1, 1}; // Y changes for N, S, and diagonals

        for (int i = 0; i < directionsX.length; i++) {
            // currentX and currentY represent the x, y coordinate pairs of the current queen being observed
            int currentX = x + directionsX[i]; // by adding the direction vector, we are effectively checking if we can make that move
            int currentY = y + directionsY[i];

            // we need to keep moving in this direction until we hit the edge of the board or an occupied tile (either burned or an opponent is already there)
            // by checking if the next position is a 0, we check if it is unoccupied
            while (currentX >= 0 && currentX < DEFAULT_BOARD_SIZE && currentY >= 0 && currentY < DEFAULT_BOARD_SIZE && boardValues[currentX][currentY] == 0) {
                legalMoves.add(new Position(currentX, currentY));
                currentX += directionsX[i];
                currentY += directionsY[i];
            }
        }

        return legalMoves;
    }

    public void performMove(int player, Position p) {
        boardValues[p.getX()][p.getY()] = player; // player will be a flag that is either 1 or 2 to denote a queen
    }

    public int checkStatus() {
        return IN_PROGRESS; // this will have to be updated each time we try to make a move
    }

    public List<Position> getEmptyPositions() {
        List<Position> emptyPositions = new ArrayList<>();
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++) {
                if (boardValues[i][j] == 0)
                    emptyPositions.add(new Position(i, j));
            }
        }
        return emptyPositions;
    }
}
