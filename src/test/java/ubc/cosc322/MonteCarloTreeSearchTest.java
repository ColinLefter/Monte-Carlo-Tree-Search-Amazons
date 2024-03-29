//package ubc.cosc322;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.jupiter.api.Test;
//import ubc.cosc322.algorithms.MonteCarloTreeSearch;
//import ubc.cosc322.core.Board;
//
//public class MonteCarloTreeSearchTest {
//    @Test
//    void givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw() {
//        Board board = new Board();
//        MonteCarloTreeSearch MCTS = new MonteCarloTreeSearch();
//        int player = Board.P1;
//        int totalMoves = Board.DEFAULT_BOARD_SIZE * Board.DEFAULT_BOARD_SIZE;
//
//        for (int i = 0; i < totalMoves; i++) {
//            board = MCTS.findNextMove(Board.getBoard(), player);
//            if (board.checkStatus(currentNode) != Board.IN_PROGRESS) {
//                break;
//            }
//            player = 3 - player;  // Toggle player
//        }
//
//        int winStatus = board.checkStatus(currentNode);
//        assertEquals(Board.DRAW, winStatus);
//    }
//}
