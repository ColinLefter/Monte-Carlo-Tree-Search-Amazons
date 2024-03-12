package ubc.cosc322.core.actionFactory;

import ubc.cosc322.core.actionFactory.GameState;

import java.util.ArrayList;

public class ActionFactory {

    private final int BOARD_SIDE_LENGTH = 10;
    public ubc.cosc322.core.actionFactory.GameState state;
    public ArrayList<Action> actions; // This is overwritten constantly, stored globally in class for ease of use

    public ArrayList<Action> getActions(ArrayList<Integer> gameState, int type) {

        // Assuming that gameState is as provideed by server
        actions = new ArrayList<>();

        // Convert to an easier format to work with, if state is not yet stored
        if (gameState != null)
            state = new ubc.cosc322.core.actionFactory.GameState(gameState);

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
