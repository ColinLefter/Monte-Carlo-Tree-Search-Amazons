package ubc.cosc322.algorithms;

import java.util.*;

import ubc.cosc322.core.Board;
import ubc.cosc322.core.Position;

/**
 * Implements the Monte Carlo Tree Search (MCTS) algorithm for the Game of the Amazons.
 * The class iteratively builds a game tree, evaluates the possible moves using simulations, and selects the best move based on the simulation outcomes.
 */
public class MonteCarloTreeSearch {
    // Lists to hold the positions of black and white queens on the board.
    final String OPPONENT = "white"; // Assumed opponent color.
    static final int WIN_SCORE = 10; // Score indicating a win in simulations.
    int level; // Represents the current level in the tree.
    final int UPPER_TIME_LIMIT = 29000;

    /**
     * Sends a move message to the game server with the specified queen positions and the arrow position.
     *
     * @param queenPosCurrent The current position of the queen.
     * @param queenPosNew The new position of the queen.
     * @param arrowPos The position where the arrow is shot.
     */
    public void sendMoveMessage(java.util.ArrayList<java.lang.Integer> queenPosCurrent,
                                java.util.ArrayList<java.lang.Integer> queenPosNew,
                                java.util.ArrayList<java.lang.Integer> arrowPos) {
    }

    /**
     * Finds the next best move using the MCTS algorithm.
     *
     * @param board The current game board.
     * @param playerNo The player number.
     * @return The updated board after the best move is applied.
     */
    public Board findNextMove(Board board, int playerNo) { // This method now uses the Tree class
        long end = System.currentTimeMillis() + UPPER_TIME_LIMIT;

        Node rootNode = new Node(playerNo);
        rootNode.setState(board.clone());
        Tree searchTree = new Tree(rootNode); // Instantiating Tree with the rootNode.

        while (System.currentTimeMillis() < end) {
            Node promisingNode = selectPromisingNode(searchTree.getRoot()); // Use Tree's root.
            if (promisingNode.getState().checkStatus() == Board.IN_PROGRESS) {
                expandNode(promisingNode, 3 - promisingNode.getPlayerNo());
            }
            Node nodeToExplore = promisingNode;
            if (!nodeToExplore.getChildren().isEmpty()) {
                nodeToExplore = nodeToExplore.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropagation(nodeToExplore, playoutResult, playerNo);
        }

        return searchTree.getRoot().getChildWithMaxScore().getState(); // Accessing root from Tree.
    }



    /**
     * Selects the most promising node to explore based on the UCT value.
     *
     * @param node The node from which to select the promising node.
     * @return The selected promising node.
     */
    public Node selectPromisingNode(Node node) {
        //node with the highest amount of playouts is returned
        Node promisingNode = node;
        while (!promisingNode.getChildArray().isEmpty()) {     //while there are still children left to explore
            promisingNode = UCT.findBestNodeWithUCT(promisingNode);
        }
        return promisingNode;
    }

    /**
     * Simulates a random playout from the given node.
     *
     * @param node The node from which the simulation starts.
     * @return The result of the simulation indicating a win, loss, or draw.
     */
    private int simulateRandomPlayout(Node node) {
        Board tempState = node.getState().clone();
        int boardStatus = tempState.checkStatus();

        if (boardStatus == 3 - node.getPlayerNo()) {
            // If the opponent wins directly, consider this a very unfavorable state
            node.getParent().addScore(Integer.MIN_VALUE);
            return boardStatus;
        }

        while (boardStatus == Board.IN_PROGRESS) {
            tempState.togglePlayer(); // We update who is the current player each time
            tempState.randomPlay();
            boardStatus = tempState.checkStatus();
        }

        return boardStatus;
    }


    /**
     * Backpropagates the result of the simulation up the tree, updating the statistics of the nodes.
     *
     * @param node The node from which to start backpropagation.
     * @param playoutResult The result of the playout to be backpropagated.
     * @param playerNo The player number associated with each node
     */
    public void backPropagation(Node node, int playoutResult, int playerNo) {
        while (node != null) {
            node.incrementVisit();
            // Only add score if the playout result corresponds to the node's player winning
            if (node.getPlayerNo() == playerNo && playoutResult == WIN_SCORE) {
                node.addScore(WIN_SCORE);
            }
            node = node.getParent();
        }
    }

    /**
     * Expands the node by creating new child nodes representing possible future game states.
     * Changed the type of list from state to board - Jared W.
     *
     * @param node The node to expand.
     */
    private void expandNode(Node node, int playerNo) {
        List<Board> possibleStates = node.getState().getAllPossibleStates(playerNo);
        for (Board state : possibleStates) {
            List<Position> queenPositions = state.getQueenPositions(playerNo);

            for (Position queenPos : queenPositions) {
                List<Position> possibleArrowShots = state.getLegalMoves(queenPos.getX(), queenPos.getY());
                for (Position arrowShot : possibleArrowShots) { // we are now considering all possible subsequent arrow shots with each expansion
                    Board newState = state.clone();
                    newState.shootArrow(queenPos, arrowShot);
                    Node childNode = new Node(3 - playerNo);
                    childNode.setState(newState);
                    node.addChild(childNode);
                }
            }
        }
    }

}