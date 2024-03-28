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
    public static double uctValue(int totalVisit, double nodeWinScore, int nodeVisit, int nodeDepth) {
        System.out.println("UCT Values- totalVisit: "+totalVisit+" nodeWinScore: "+nodeWinScore+" nodeVisit: "+nodeVisit+" nodeDepth: "+nodeDepth);
        double explorationConstant = 1.41; // Initial exploration constant
        // Dynamically adjust the exploration constant based on the node depth
        explorationConstant = adjustExplorationConstant(explorationConstant, nodeDepth);

        if (nodeVisit == 0) {
            return Integer.MAX_VALUE; // Encourage exploration of unvisited nodes
        }
        // UCT formula with dynamically adjusted exploration constant
        return (nodeWinScore / (double) nodeVisit)
                + explorationConstant * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static double adjustExplorationConstant(double explorationConstant, int nodeDepth) {
        // Example adjustment strategy: linear decrease with depth
        // Define a depth threshold beyond which we will adjust the exploration constant
        int depthThreshold = 10; // This threshold can be adjusted based on experimentation
        double decreaseFactor = 0.05; // How much we decrease the exploration constant per depth unit beyond the threshold

        if (nodeDepth > depthThreshold) {
            // Calculate the decrease amount based on how far we are beyond the threshold
            double decreaseAmount = (nodeDepth - depthThreshold) * decreaseFactor;
            // Apply the decrease but ensure the exploration constant does not go below a minimum value (e.g., 0.1)
            double adjustedConstant = Math.max(explorationConstant - decreaseAmount, 0.1);
            return adjustedConstant;
        }

        // If node depth is not beyond the threshold, return the original exploration constant
        return explorationConstant;
    }

    /**
     * Finds and returns the best node to explore next based on the UCT values of its children.
     *
     * @param node The node whose children are to be evaluated.
     * @return The child node with the highest UCT value.
     */
    public static Node findBestNodeWithUCT(Node node) {
        // Selects the child node with the maximum UCT value.
        return Collections.max(node.getChildArray(),
                Comparator.comparing(c -> uctValue(c.getVisitCount(), c.getScore(), c.getVisitCount(), c.getNodeDepth())));
    }
}