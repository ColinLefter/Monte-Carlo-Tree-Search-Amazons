package ubc.cosc322.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ubc.cosc322.algorithms.BFSAmazons;

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
    private static final int ARROW = 3;
    public static final int DRAW = 0;
    public static final int P1 = 1; // this is subject to who joins first. 1 represents black
    public static final int P2 = 2;

    // By introducing a currentPlayer variable at the board level, we can keep track of who is currently playing on the board. Must be updated throughout the game's progression.
    private int currentPlayer = P1; // P1 always starts the game (black). We just need to know who is P1.

    // Lists to keep track of player positions.
    // Excellent optimization to avoid O(n^2) runtime complexity of a solution where we scan the board each time
    private List<Position> player1Positions = new ArrayList<>();
    private List<Position> player2Positions = new ArrayList<>();

    /**
     * Initializes a new Board instance with default size.
     */
    public Board() {
        this.boardValues = new int[DEFAULT_BOARD_SIZE][DEFAULT_BOARD_SIZE]; // 10 x 10 board
        initializePositions(); // We are now initializing positions in the Board class instead of in MCTS.
    }

    private void initializePositions() {
        // Initial positions for black queens
        player1Positions.add(new Position(0, 6));
        player1Positions.add(new Position(3, 9));
        player1Positions.add(new Position(6, 9));
        player1Positions.add(new Position(9, 6));
        for (Position pos : player1Positions) {
            boardValues[pos.getX()][pos.getY()] = 1;
        }

        // Initial positions for white queens
        player2Positions.add(new Position(0, 3));
        player2Positions.add(new Position(3, 0));
        player2Positions.add(new Position(6, 0));
        player2Positions.add(new Position(9, 3));
        for (Position pos : player2Positions) {
            boardValues[pos.getX()][pos.getY()] = 2;
        }
    }

    public List<Position> getQueenPositions(int playerNo) {
        if (playerNo == P1) { // IMPORTANT: We are returning copies as otherwise we would be returning the original object that can be modified
            return new ArrayList<>(player1Positions);  // Returns a copy of the player 1 queen positions
        } else if (playerNo == P2) {
            return new ArrayList<>(player2Positions);  // Returns a copy of the player 2 queen positions
        } else {
            throw new IllegalArgumentException("Invalid player number.");
        }
    }


    /**
     * Clones this Board instance, creating a new instance with the same board state.
     *
     * @return A new Board instance with the same state as this board.
     */
    public Board clone() {
        Board newBoard = new Board();
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {
            newBoard.boardValues[i] = this.boardValues[i].clone();
        }
        newBoard.player1Positions = new ArrayList<>(this.player1Positions); // We are also resetting the player positions
        newBoard.player2Positions = new ArrayList<>(this.player2Positions);
        return newBoard;
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
     * @param priorPos Where our queen used to be.
     * @param newPos Where our queen is now.
     */

    public void performMove(int player, Position priorPos, Position newPos) {
        boardValues[priorPos.getX()][priorPos.getY()] = 0;
        boardValues[newPos.getX()][newPos.getY()] = player;

        List<Position> positions = player == 1 ? player1Positions : player2Positions;
        positions.remove(priorPos);
        positions.add(newPos);
    }

    public void shootArrow(Position from, Position to) {
        boardValues[to.getX()][to.getY()] = ARROW; // Using the 3 flag to denote an arrow
    }

    /**
     * Checks the current status of the game board.
     *
     * @return An integer representing the game status (IN_PROGRESS, DRAW, P1 win, or P2 win).
     */
    public int checkStatus() {
        // For each queen(of one side), check the following.
        // 1. Can the queen move?
        // 2. If yes, can the queen find a queen of the opposite color?
        // 3a. If yes, the game is in progress, so return.
        // 3b. If no, count the available squares for each colour. Use the counts to determine the results.
        
        BFSAmazons search = new BFSAmazons();

        int queenVal = 1; // Search for black queens first
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++) {

                if (boardValues[i][j] == queenVal) {
                    // Queen found. Increment, start searching rest of board (BFS).
                    if (search.searchBoardPartition(boardValues, i, j, queenVal) == 0)
                        return IN_PROGRESS;

                }
            }
        }

        // If this part is reached, the game is determined by score. Repeat with white queens.
        queenVal = 2;
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++) {

                if (boardValues[i][j] == queenVal) {
                    // Queen found. Increment, start searching rest of board (BFS). Don't bother checking return value.
                    search.searchBoardPartition(boardValues, i, j, queenVal);
                }
            }
        }
        // Return result
        if (search.totalWhiteCount == search.totalBlackCount) {
            return DRAW;
        } else if(search.totalBlackCount > search.totalWhiteCount) {
            return P1;
        } else {
            return P2;
        }
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

    public void togglePlayer() {
        // Assuming currentPlayer is an int that represents the player (1 or 2).
        currentPlayer = currentPlayer == 1 ? 2 : 1;
    }

    public int getCurrentPlayer()  {
        return currentPlayer;
    }

    public void randomPlay() {
        List<Position> playerPositions = currentPlayer == P1 ? player1Positions : player2Positions;
        Random random = new Random();

        if (!playerPositions.isEmpty()) {
            Position piecePosition = playerPositions.get(random.nextInt(playerPositions.size()));
            List<Position> legalMoves = getLegalMoves(piecePosition.getX(), piecePosition.getY());

            if (!legalMoves.isEmpty()) {
                Position selectedMove = legalMoves.get(random.nextInt(legalMoves.size()));
                performMove(currentPlayer, piecePosition, selectedMove);
            }
        }
    }

    private List<Position> getPlayerPositions(int player) {
        List<Position> positions = new ArrayList<>();
        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++) {
                if (boardValues[i][j] == player) { // We need to know who is where
                    positions.add(new Position(i, j));
                }
            }
        }
        return positions;
    }
}
