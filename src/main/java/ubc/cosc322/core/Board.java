package ubc.cosc322.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Represents the game board for the Game of the Amazons.
 * Provides functionality to track and update the board state,
 * including calculating legal moves, performing moves, and checking the game status.
 */
public class Board {
    public static int randomPlays = 0;
    public static int gamesPlayed = 0;
    // The 2D array representing the board state; 0 for empty, 1 for player 1, and 2 for player 2.
    private int[][] boardValues;
    private static int[][] mainBoardValues = new int[10][10];
    public static final int DEFAULT_BOARD_SIZE = 10;
    public static final int IN_PROGRESS = -1;
    public static final int DRAW = 0;
    public static final int P1 = 1; // this is subject to who joins first. 1 represents black
    public static final int P2 = 2;
    public static final int ARROW = 3;

    // By introducing a currentPlayer variable at the board level, we can keep track of who is currently playing on the board. Must be updated throughout the game's progression.
    private static int currentPlayer = P1; // P1 always starts the game (black). We just need to know who is P1.

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
    // Initialize the board from an ArrayList<Integer>

    public List<Position> getQueenPositions(int playerNo) {
        List<Position> queenPositions = new ArrayList<>();

        for (int i = 0; i < DEFAULT_BOARD_SIZE; i++) {  //row
            for (int j = 0; j < DEFAULT_BOARD_SIZE; j++) { //column
                if((boardValues[i][j]) == playerNo){
                    queenPositions.add(new Position(i,j));
                }
            }
        }
        return queenPositions;
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
            while (currentX >= 0 && currentX < DEFAULT_BOARD_SIZE && currentY >= 0 && currentY < DEFAULT_BOARD_SIZE && this.boardValues[currentX][currentY] == 0) {
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
     * @param newPos The position to which the player is moving.
     */
    public void performMove(int player, Position currentPos, Position newPos) {
        // Remove the piece from its current position.
        this.boardValues[currentPos.getX()][currentPos.getY()] = 0;

        // Place the piece at the new position.
        this.boardValues[newPos.getX()][newPos.getY()] = player;

        // Update the position list.
        if (player == P1) {
            player1Positions.remove(currentPos);
            player1Positions.add(newPos);
        } else {
            player2Positions.remove(currentPos);
            player2Positions.add(newPos);
        }
    }

    /**
     * Checks the current status of the game board.
     *
     * @return An integer representing the game status (IN_PROGRESS, DRAW, P1 win, or P2 win).
     */
// Make sure checkStatus is an instance method if it's going to call other instance methods like getLegalMoves
    public int checkStatus() {
        boolean blackHasMoves = false;
        boolean whiteHasMoves = false;

        // Use this to call instance methods
        for (int x = 0; x < DEFAULT_BOARD_SIZE; x++) {
            for (int y = 0; y < DEFAULT_BOARD_SIZE; y++) {
                int piece = this.boardValues[x][y];
                if (piece == P1 || piece == P2) {
                    List<Position> legalMoves = this.getLegalMoves(x, y);
                    //System.out.println("Debug: queen " +piece+ "at position "+x+","+y+" has moves " + legalMoves);
                    if (!legalMoves.isEmpty()) {
                        if (piece == P1) blackHasMoves = true;
                        else whiteHasMoves = true;
                    }
                }
            }
        }

        if (blackHasMoves && !whiteHasMoves) {
            //System.out.println("Debug: Black wins");
            //printBoard(this.getBoard());
            gamesPlayed++;
            return P1; // Black wins
        }
        else if (!blackHasMoves && whiteHasMoves) {
            ///System.out.println("Debug: White wins");
            //printBoard(this.getBoard());
            gamesPlayed++;
            return P2; // White wins
        } else if(!blackHasMoves && !whiteHasMoves){
            //System.out.println("Debug: White & Black don't have moves");
            gamesPlayed++;
            return DRAW;
        }
        else return IN_PROGRESS; // Game is still in progress or it's a draw
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
     * @param isPlayerWhite A boolean indicating whether the AI is playing as white.
     * @return The player number (1 for black, 2 for white).
     */
    public static int getBoardPlayerNo(boolean isPlayerWhite) {
        currentPlayer = isPlayerWhite ? P2 : P1;
        return currentPlayer;
    }

    /**
     * Generates all possible next states of the board from the current player's perspective.
     *
     * @param currentPlayer The player number (P1 or P2) for whom to generate possible states.
     * @return A list of Board objects representing all possible next states.
     */
    public List<Board> getAllPossibleStates(int currentPlayer) {
        List<Board> possibleStates = new ArrayList<>();
        // For each queen, generate states after moving the queen and shooting an arrow.
        for (Position queenPos : getQueenPositions(currentPlayer)) {
            List<Position> queenMoves = getLegalMoves(queenPos.getX(), queenPos.getY());
            for (Position queenMove : queenMoves) {
                Board stateAfterQueenMove = this.clone();
                stateAfterQueenMove.performMove(currentPlayer, queenPos, queenMove);
                List<Position> arrowShots = stateAfterQueenMove.getLegalMoves(queenMove.getX(), queenMove.getY());
                for (Position arrowShot : arrowShots) {
                    Board finalState = stateAfterQueenMove.clone();
                    finalState.shootArrow(arrowShot);
                    possibleStates.add(finalState);
                }
            }
        }
        return possibleStates;
    }

    public static void printBoard(int [][] printBoard) {
        System.out.println();
        for (int i = 9; i > -1; i--) { // Iterate through each row
            for (int j = 0; j < 10; j++) { // Iterate through each column in the row
                System.out.print(printBoard[i][j] + " "); // Print the value at the current position
            }
            System.out.println(); // Move to the next line after printing each row
        }
        System.out.println();
    }

    public void randomPlay(int playerNo) {
        Random random = new Random();
        // Determine the current player's positions
        List<Position> playerPositions = getQueenPositions(playerNo);
        if (!playerPositions.isEmpty()) {
            // Choose a random queen from the current player's positions
            Position piecePosition = playerPositions.get(random.nextInt(playerPositions.size()));
            // Find all legal moves for that queen
            List<Position> legalMoves = getLegalMoves(piecePosition.getX(), piecePosition.getY());

            if (!legalMoves.isEmpty()) {
                // Select one of the legal moves at random
                Position selectedMove = legalMoves.get(random.nextInt(legalMoves.size()));
                performMove(playerNo, piecePosition, selectedMove);
                // After moving, find all possible positions to shoot the arrow
                List<Position> arrowShots = getLegalMoves(selectedMove.getX(), selectedMove.getY());
                if (!arrowShots.isEmpty()) {
                    // Select a random position for the arrow
                    Position arrowPosition = arrowShots.get(random.nextInt(arrowShots.size()));
                    this.boardValues[arrowPosition.getX()][arrowPosition.getY()] = ARROW;
                }
            }
        }
    }

    public void shootArrow(Position arrowPosition) {
        // Check if the position is within the bounds of the board
        if(arrowPosition.getX() >= 0 && arrowPosition.getX() < DEFAULT_BOARD_SIZE &&
                arrowPosition.getY() >= 0 && arrowPosition.getY() < DEFAULT_BOARD_SIZE) {
            // Mark the position with a 3 to indicate an arrow
            boardValues[arrowPosition.getX()][arrowPosition.getY()] = ARROW;
        } else {
            System.out.println("Arrow position is out of bounds.");
        }
    }

    public static ArrayList<Integer> extractMoveDetails(Board currentBoard, Board bestMoveBoard) {
        ArrayList<Integer> moveDetails = new ArrayList<>();

        // Initialize variables to track the positions found.
        Integer oldQueenX = null, oldQueenY = null, newQueenX = null, newQueenY = null, arrowX = null, arrowY = null;

        // Loop over the board to identify the old queen position, new queen position, and arrow position.
        for (int x = 0; x < DEFAULT_BOARD_SIZE; x++) {
            for (int y = 0; y < DEFAULT_BOARD_SIZE; y++) {
                if (currentBoard.boardValues[x][y] != bestMoveBoard.boardValues[x][y]) {
                    if (currentBoard.boardValues[x][y] != 0) {
                        if(bestMoveBoard.boardValues[x][y] == 3){
                            oldQueenX = x + 1;
                            oldQueenY = y + 1;
                            arrowX = x + 1;
                            arrowY = y + 1;
                        } else{
                            // The queen has moved from this position.
                            oldQueenX = x + 1;
                            oldQueenY = y + 1;
                        }
                    } else if (currentBoard.boardValues[x][y] == 0 && bestMoveBoard.boardValues[x][y] != 0 && bestMoveBoard.boardValues[x][y] != ARROW) {
                        // The queen has moved to this position.
                        newQueenX = x + 1;
                        newQueenY = y + 1;
                    } else if (bestMoveBoard.boardValues[x][y] == ARROW) {
                        // The arrow has been shot to this position.
                        arrowX = x + 1;
                        arrowY = y + 1;
                    }
                }
            }
        }

        // Compile and return the move details if all components are identified.
        if (oldQueenX != null && oldQueenY != null && newQueenX != null && newQueenY != null && arrowX != null && arrowY != null) {
            moveDetails.addAll(Arrays.asList(oldQueenX, oldQueenY, newQueenX, newQueenY, arrowX, arrowY));
            return moveDetails;
        } else if (oldQueenX == null && oldQueenY == null && newQueenX == null && newQueenY == null && arrowX == null && arrowY == null) {
            return moveDetails;
        } else {
            // Log missing components for debugging purposes.
            System.err.println("Missing move components: oldQ=(" + oldQueenX + "," + oldQueenY +
                    "), newQ=(" + newQueenX + "," + newQueenY +
                    "), arrow=(" + arrowX + "," + arrowY + ")");
            throw new IllegalStateException("Failed to extract move details. Missing components.");
        }
    }

    public static int getCurrentPlayer() {
        return currentPlayer;
    }

    public void printMainBoard() {
        for (int i = 9; i > -1; i--) { // Iterate through each row
            for (int j = 0; j < 10; j++) { // Iterate through each column in the row
                System.out.print(mainBoardValues[i][j] + " "); // Print the value at the current position
            }
            System.out.println(); // Move to the next line after printing each row
        }
    }

    public static void updateMainBoard(ArrayList<Integer> currentPosition,
                                       ArrayList<Integer> nextPosition,
                                       ArrayList<Integer> arrowPosition) {
        int currentX = currentPosition.get(0) - 1;
        int currentY = currentPosition.get(1) - 1;
        int nextX = nextPosition.get(0) - 1;
        int nextY = nextPosition.get(1) - 1;
        int arrowX = arrowPosition.get(0) - 1;
        int arrowY = arrowPosition.get(1) - 1;
        int player = mainBoardValues[currentX][currentY];
        mainBoardValues[currentX][currentY] = 0;
        mainBoardValues[nextX][nextY] = player;
        mainBoardValues[arrowX][arrowY] = 3;
    }

    public static void setMainBoard(ArrayList<Integer> gameBoardState) {
        int[][] array = new int[10][10];
        for(int i = 1; i < 11; i++){
            for(int j = 1; j < 11; j++){
                array[i-1][j-1] = gameBoardState.get(11*i + j);
            }
        }
        // Update mainBoardValues with the new 2D array
        mainBoardValues = array;
    }

    public static Board getMainBoard(){
        Board board = new Board();
        board.setBoard(mainBoardValues);
        return board;
    }
}
