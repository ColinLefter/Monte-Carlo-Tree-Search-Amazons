package ubc.cosc322.algorithms;

import ubc.cosc322.core.Board;
import java.util.Collections;
import java.util.Comparator;

public class UCT {
    /**
     * Calculates the UCT value for a given node.
     * This value is used to determine which node to explore next in the MCTS algorithm,
     * balancing between exploring new nodes and exploiting known good ones.
     *
     * @param nodeWinScore The win score of the node being evaluated.
     * @param nodeVisit The number of visits to the node being evaluated.
     * @return The calculated UCT value.
     */
    public static double uctValue(double nodeWinScore, int nodeVisit) {
        double explorationConstant = 1.41; // Initial exploration constant

        if (nodeVisit == 0) {
            return Integer.MAX_VALUE; // Encourage exploration of unvisited nodes
        }
        // UCT formula without depth adjustment
        return (nodeWinScore / (double) nodeVisit)
                + explorationConstant * Math.sqrt(Math.log(nodeVisit) / (double) nodeVisit);
    }

    /**
     * Finds and returns the best node to explore next based on the UCT values of its children.
     * If the node has no children, returns null.
     *
     * @param node The node whose children are to be evaluated.
     * @return The child node with the highest UCT value, or null if there are no children.
     */
    public static Node findBestNodeWithUCT(Node node) {
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            //System.out.println("No children available for this node.");
            return null; // No children, so no move can be made.
        }

        // Proceed as before if there are children.
        for (Node child : node.getChildren()) {
            System.out.println("Child UCT Values: Score - " + child.getScore() + " Visit Count - "+ child.getVisitCount());
        }

        return Collections.max(node.getChildren(),
                Comparator.comparing(c -> uctValue(c.getScore(), c.getVisitCount())));
    }
}