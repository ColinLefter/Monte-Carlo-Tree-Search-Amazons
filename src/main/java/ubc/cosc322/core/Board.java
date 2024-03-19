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
    public static final int P1 = 1; // this is subject to who joins first. 1 represents black
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

    /**
     * Retrieves the current state of the board.
     *
     * @return The 2D array representing the board state.
     */
    public int[][] getBoard() {
        return boardValues;
    }

    /**
     * Sets the board state. Use with caution to avoid corrupting the game state.
     *
     * @param newBoardValues The new board state to set.
     */
    public void setBoard(int[][] newBoardValues) {
        this.boardValues = newBoardValues;
    }

    /**
     * Retrieves the player number based on the player name.
     * The name 'CKJJA' corresponds to the AI player (our initials put together) and determines the player number based on the order of joining.
     * We need to know two things to determine who is who: the name of the white player, and whether that name is what we named our AI
     *
     * @param playerName The name of the player being checked.
     * @param isPlayerWhite A boolean indicating whether the AI is playing as white.
     * @return The player number (1 for black, 2 for white).
     */
    public int getPlayerNo(String playerName, boolean isPlayerWhite) {
        if (playerName.equals("CKJJA")) {
            return isPlayerWhite ? P2 : P1; // If AI is white, return P2; otherwise, P1
        } else {
            return isPlayerWhite ? P1 : P2; // If AI is white, return P1 for the opponent; otherwise, P2
        }
    }

    /**
     * @param currentPlayer The player number of the current player.
     * @return The opponent's player number.
     */
    public int getOpponent(int currentPlayer) {
        // Assuming only two players, this returns the opponent's number.
        return (currentPlayer == P1) ? P2 : P1; // If we are player 1, then the opponent must be player 2
    }

    /**
     * Generates all possible next states of the board from the current player's perspective.
     *
     * @param currentPlayer The player number (P1 or P2) for whom to generate possible states.
     * @return A list of Board objects representing all possible next states.
     */
    public List<Board> getAllPossibleStates(int currentPlayer) {
        List<Board> possibleStates = new ArrayList<>();

        // Iterate over all board positions
        for (int x = 0; x < DEFAULT_BOARD_SIZE; x++) {
            for (int y = 0; y < DEFAULT_BOARD_SIZE; y++) {
                // Check if there is a queen of the current player at this position
                if (boardValues[x][y] == currentPlayer) {
                    // Get all legal moves for this queen
                    List<Position> legalMoves = getLegalMoves(x, y);

                    // For each legal move, create a new board state
                    for (Position move : legalMoves) {
                        Board newState = new Board();
                        copyBoardState(this.boardValues, newState.boardValues);

                        // Move the queen to the new position
                        newState.boardValues[x][y] = 0; // Remove from the old position. 0 denotes an open tile.
                        newState.boardValues[move.getX()][move.getY()] = currentPlayer; // Place at the new position

                        possibleStates.add(newState);
                    }
                }
            }
        }

        return possibleStates;
    }

    /**
     * Copies the board state from one 2D array to another.
     *
     * @param source The source 2D array.
     * @param destination The destination 2D array.
     */
    private void copyBoardState(int[][] source, int[][] destination) {
        for (int i = 0; i < source.length; i++) {
            System.arraycopy(source[i], 0, destination[i], 0, source[i].length);
        }
    }
}
