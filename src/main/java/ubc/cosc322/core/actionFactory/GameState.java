package ubc.cosc322.core.actionFactory;

import java.util.ArrayList;

public class GameState {

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
