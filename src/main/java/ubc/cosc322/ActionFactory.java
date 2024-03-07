package ubc.cosc322;
import java.util.ArrayList;

// Corresponds with int values of gamestate
// enum Piece {
//     NONE,
//     BLACK_QUEEN,
//     WHITE_QUEEN,
//     ARROW
// }
// Doesn't work like c++ enums >:(

public class ActionFactory {
    
    private final int BOARD_SIDE_LENGTH = 10;
    public GameState state;
    public ArrayList<Action> actions; // This is overwritten constantly, stored globally in class for ease of use
    
    public ArrayList<Action> getActions(ArrayList<Integer> gameState, int type) {

        // Assuming that gameState is as provideed by server
        actions = new ArrayList<>();

        // Convert to an easier format to work with, if state is not yet stored
        if (gameState != null)
            state = new GameState(gameState);

        // First, search for the applicable pieces based on type
        for (int i = 0; i < BOARD_SIDE_LENGTH; i++) {
            for (int j = 0; j < BOARD_SIDE_LENGTH; j++) {

                if (state.board[i][j] == type) {
                    // System.out.println(state.board[i][j]);
                    
                    // Branch with rules of a queen, clockwise
                    int directionFlags = 0;
                    int a = 1; // a is the number of squares in each direction
                    while (directionFlags != 255) { // 255 represents 8 bits flipped to 1. Each of said bits represent a direction, starting north and going CW. If a directionFlag bit is flipped high, then that direction can no longer be explored
                        
                        // N
                        if((directionFlags & 0b1) != 0b1)
                            directionFlags |= checkDirectionHelper(i, j, -1 * a, 0, 0b1);
                        // NE
                        if((directionFlags & 0b10) != 0b10)
                            directionFlags |= checkDirectionHelper(i, j, -1 * a, -1 * a, 0b10);
                        // E
                        if((directionFlags & 0b100) != 0b100)
                            directionFlags |= checkDirectionHelper(i, j, 0 * a, -1 * a, 0b100);
                        // SE
                        if((directionFlags & 0b1000) != 0b1000)
                            directionFlags |= checkDirectionHelper(i, j, 1 * a, 1 * a, 0b1000);
                        // S
                        if((directionFlags & 0b10000) != 0b10000)
                            directionFlags |= checkDirectionHelper(i, j, 1 * a, 0 * a, 0b10000);
                        // SW
                        if((directionFlags & 0b100000) != 0b100000)
                            directionFlags |= checkDirectionHelper(i, j, 1 * a, -1 * a, 0b100000);
                        // W
                        if((directionFlags & 0b1000000) != 0b1000000)
                            directionFlags |= checkDirectionHelper(i, j, 0 * a, -1 * a, 0b1000000);
                        // NW
                        if((directionFlags & 0b10000000) != 0b10000000)
                            directionFlags |= checkDirectionHelper(i, j, -1 * a, -1 * a, 0b10000000);
 
                        a++; // Increment direction multiplier
                    }
                }
            }
        }

        for(int i = 0; i < actions.size(); i++) {
            actions.get(i).displayMove(i);
        }
        System.out.println("COUNT: " + actions.size());

        return actions;
    }

    public int checkDirectionHelper(int i, int j, int x, int y, int flag) {

        if (i+x >= 0 && i+x < BOARD_SIDE_LENGTH && j+y >= 0 && j+y < BOARD_SIDE_LENGTH && state.board[i+x][j+y] == 0) {
            actions.add(new Action(i, j, i+x, j+y));
            return 0b0;
        } else {
            return flag;
        }

    }

    // TODO: Fill this in when either we or the opponent makes a move.
    public void updateGameState(Action move) {
        // Do something
    }

}

class Action {

    // TODO: Verify that this is the proper format as compared to server
    int oldX, oldY, newX, newY; // Based on board, x: 0-9, y: 0-9, top-left to bottom-right

    Action(int oldX, int oldY, int newX, int newY) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

    public void displayMove(int i) {
        System.out.println();
        System.out.println("Move #" + i);
        System.out.println("Old Position row: " + oldX + " col: " + oldY);
        System.out.println("New Position row: " + newX + " col: " + newY);
    }

}

// Local gamestate, so we don't have to parse from msg
class GameState {

    int[][] board = new int[10][10];

    GameState(ArrayList<Integer> gameState) {

        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                board[i-1][j-1] = gameState.get(11*i + j); // Pulled from setGameState(), 0 = nothing, 1 = black queen, 2 = white queen, 3 = arrow
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

}
