package ubc.cosc322.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board for the Game of the Amazons.
 * Provides functionality to track and update the board state,
 * including calculating legal moves, performing moves, and checking the game status.
 */
public class Board {
    // The 2D array representing the board state; 0 for empty, 1 for player 1, and 2 for player 2.
    int[][] boardValues;
    public static final int DEFAULT_BOARD_SIZE = 10;
    public static final int IN_PROGRESS = -1;
    public static final int DRAW = 0;
    public static final int P1 = 1;
    public static final int P2 = 2;

    /**
     * Initializes a new Board instance with default size.
     */
    public Board() {
        this.boardValues = new int[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE]; // 10 x 10 board
    }

    /**
     * Computes all legal moves for a queen located at the specified coordinates.
     *
     * @param x The x-coordinate of the queen.
     * @param y The y-coordinate of the queen.
     * @return A list of Position objects representing all legal moves for the queen.
     */
    public List<Position> getLegalMoves(int x, int y) {
        List<Position> legalMoves = new ArrayList<>();

        // Directions the queen can move, ordered as N, E, S, W, NE, SE, SW, NW
        int[] directionsX = {0, 1, 0, -1, 1, 1, -1, -1}; // X changes for E, W, and diagonals
        int[] directionsY = {1, 0, -1, 0, 1, -1, -1, 1}; // Y changes for N, S, and diagonals

        for (int i = 0; i < directionsX.length; i++) {
            // currentX and currentY represent the x, y coordinate pairs of the current queen being observed
            int currentX = x + directionsX[i]; // By adding the direction vector, we are effectively checking if we can make that move
            int currentY = y + directionsY[i];

            // We need to keep moving in this direction until we hit the edge of the board or an occupied tile (either burned or an opponent is already there)
            // By checking if the next position is a 0, we check if it is unoccupied
            while (currentX >= 0 && currentX < DEFAULT_BOARD_SIZE && currentY >= 0 && currentY < DEFAULT_BOARD_SIZE && boardValues[currentX][currentY] == 0) {
                legalMoves.add(new Position(currentX, currentY));
                currentX += directionsX[i];
                currentY += directionsY[i];
            }
        }

        return legalMoves;
    }

    /**
     * Updates the board state to reflect a move made by a player.
     *
     * @param player The player number (1 or 2) making the move.
     * @param p The position to which the player is moving.
     */
    public void performMove(int player, Position p) {
        boardValues[p.getX()][p.getY()] = player;
    }

    /**
     * Checks the current status of the game board.
     *
     * @return An integer representing the game status (IN_PROGRESS, DRAW, P1 win, or P2 win).
     */
    public int checkStatus() {
        return IN_PROGRESS; // Placeholder implementation; should be extended to check actual game status.
    }

    /**
     * Retrieves all empty positions on the board.
     *
     * @return A list of Position objects representing all unoccupied spaces on the board.
     */
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

    public Board getBoard() {
        //TODO: get the state of the board
        return null;
    }

    public void setBoard(Board board) {
        //TODO: set the state of the board
    }

    public void setPlayerNo(int opponent) {
        //TODO: set the player number
    }

    public int getOpponent() {
        //TODO: get opponents position on board
        return 0;
    }

    public List<Board> getAllPossibleStates() {
        //TODO: retrieve all possible states of the board
        return null;
    }
}
