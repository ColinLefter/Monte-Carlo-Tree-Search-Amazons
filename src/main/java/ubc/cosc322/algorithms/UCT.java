package ubc.cosc322.algorithms;


import ubc.cosc322.core.Board;
import java.util.Collections;
import java.util.Comparator;

/**
 * Utility class providing methods to apply the Upper Confidence Bound 1 applied to Trees (UCT) algorithm.
 * UCT is used within MCTS to balance exploration and exploitation by evaluating the potential value of each node.
 */
public class UCT {
    /**
     * Calculates the UCT value for a given node.
     * This value is used to determine which node to explore next in the MCTS algorithm, balancing
     * between exploring new nodes and exploiting known good ones.
     *
     * @param totalVisit The total number of visits to the parent node.
     * @param nodeWinScore The win score of the node being evaluated.
     * @param nodeVisit The number of visits to the node being evaluated.
     * @return The calculated UCT value.
     */
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE; // Encourage exploration of unvisited nodes.
        }
        // UCT formula: combines the win rate with a term that encourages exploration based on the number of visits.
        return (nodeWinScore / (double) nodeVisit)
                + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    /**
     * Finds and returns the best node to explore next based on the UCT values of its children.
     *
     * @param node The node whose children are to be evaluated.
     * @return The child node with the highest UCT value.
     */
    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getVisitCount();
        // Selects the child node with the maximum UCT value.
        return Collections.max(node.getChildArray(),
                Comparator.comparing(c -> uctValue(parentVisit, c.getScore(), c.getVisitCount())));
    }

    private int simulateRandomPlayout(Node node, int opponent) {
        Board tempState = node.getState().clone(); // Clone the board state.
        int boardStatus = tempState.checkStatus();

        if (boardStatus == opponent) {
            // If immediate loss is detected, discourage this path.
            node.getParent().addScore(Integer.MIN_VALUE);
            return boardStatus;
        }

        while (boardStatus == Board.IN_PROGRESS) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.checkStatus();
        }

        return boardStatus;
    }

}