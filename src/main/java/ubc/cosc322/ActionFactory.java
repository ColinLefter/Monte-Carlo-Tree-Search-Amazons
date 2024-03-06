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
    
    public ArrayList<Action> getActions(ArrayList<Integer> gameState, int type) {

        // Assuming that gameState is as provideed by server
        ArrayList<Action> actions = new ArrayList<>();

        // Convert to an easier format to work with
        if (gameState == null)
            state = new GameState(gameState);

        // First, search for the applicable pieces
        for (int i = 0; i < BOARD_SIDE_LENGTH; i++) {
            for (int j = 0; j < BOARD_SIDE_LENGTH; j++) {

                if (state.board[i][j] == type) {
                    // Branch like a queen, like a clock
                    int directionFlags = 0;
                    int a = 1, b = 1, x, y;
                    while (directionFlags != 255) {

                        // Gross, but it should work

                        // N
                        if((directionFlags & 0b1) == 0b1) {
                            x = 0 * a;
                            y = -1 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b1);
                        }

                        // NE
                        if((directionFlags & 0b10) == 0b10) {
                            x = 1 * a;
                            y = -1 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b10);
                        }

                        // E
                        if((directionFlags & 0b100) == 0b100) {
                            x = 1 * a;
                            y = 0 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b100);
                        }

                        // SE
                        if((directionFlags & 0b1000) == 0b1000) {
                            x = 1 * a;
                            y = 1 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b1000);
                        }

                        // S
                        if((directionFlags & 0b10000) == 0b10000) {
                            x = 0 * a;
                            y = 1 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b10000);
                        }

                        // SW
                        if((directionFlags & 0b1000000) == 0b1000000) {
                            x = -1 * a;
                            y = 1 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b100000);
                        }

                        // W
                        if((directionFlags & 0b10000000) == 0b10000000) {
                            x = -1 * a;
                            y = 0 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b1000000);
                        }

                        // NW
                        if((directionFlags & 0b100000000) == 0b100000000) {
                            x = -1 * a;
                            y = -1 * b;
                            if (state.board[i+x][j+y] == 0)
                                actions.add(new Action(i, j, i+a, j+b));
                            else
                                directionFlags |= (directionFlags & 0b1000000);
                        }     
                        
                        a++;
                        b++;

                    }

                }


            }
        }


        return actions;
    }

    public void updateGameState(Action move) {
        // Do something
    }

}

class Action {

    int oldX, oldY, newX, newY; // Based on board, x: 0-9, y: 0-9

    Action(int oldX, int oldY, int newX, int newY) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

}

// Local gamestate, so we don't have to parse from msg
class GameState {

    int[][] board = new int[10][10];

    GameState(ArrayList<Integer> gameState) {

        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                board[i-1][j-1] = gameState.get(11*j + 1); // Pulled from setGameState(), 0 = nothing, 1 = black queen, 2 = white queen, 3 = arrow
            }
        }
    }

}
