package ubc.cosc322.algorithms;

import java.util.*;

import ubc.cosc322.core.Board;

/**
 * Implements the Monte Carlo Tree Search (MCTS) algorithm for the Game of the Amazons.
 * The class iteratively builds a game tree, evaluates the possible moves using simulations, and selects the best move based on the simulation outcomes.
 */
public class MonteCarloTreeSearch {
    // Lists to hold the positions of black and white queens on the board.
    List<List<Integer>> blackPositions = new ArrayList<>();
    List<List<Integer>> whitePositions = new ArrayList<>();
    final String OPPONENT = "white"; // Assumed opponent color.
    static final int WIN_SCORE = 10; // Score indicating a win in simulations.
    int level; // Represents the current level in the tree.
    final int UPPER_TIME_LIMIT = 29000;

    /**
     * Initializes the MonteCarloTreeSearch object and sets up the initial positions of the queens on the board.
     */
    public MonteCarloTreeSearch() {
        initializePositions();
    }

    /**
     * Initializes the starting positions of the queens on the board for both players.
     */
    public void initializePositions() {
        // Initialize black queen positions.
        blackPositions.add(Arrays.asList(1, 7));
        blackPositions.add(Arrays.asList(4, 10));
        blackPositions.add(Arrays.asList(7, 10));
        blackPositions.add(Arrays.asList(10, 7));

        // Initialize white queen positions.
        whitePositions.add(Arrays.asList(1, 4));
        whitePositions.add(Arrays.asList(4, 1));
        whitePositions.add(Arrays.asList(7, 1));
        whitePositions.add(Arrays.asList(10, 4));
    }

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
    public Board findNextMove(Board board, int playerNo) {
        long end = System.currentTimeMillis() + UPPER_TIME_LIMIT;

        Node rootNode = new Node(playerNo); // Create a root node with the current player number.
        rootNode.setState(board); // Set the initial state of the game.

        while (System.currentTimeMillis() < end) {
            Node promisingNode = selectPromisingNode(rootNode);
            if (promisingNode.getState().checkStatus() == Board.IN_PROGRESS) {
                // When expanding, we use the opponent of the node's player because each level alternates.
                expandNode(promisingNode, 3 - promisingNode.getPlayerNo());
            }
            Node nodeToExplore = promisingNode;
            if (!promisingNode.getChildren().isEmpty()) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropagation(nodeToExplore, playoutResult, playerNo); // Pass playerNo for correct score assignment.
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        return winnerNode.getState();
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
     * @param toExplore The node from which the simulation starts.
     * @return The result of the simulation indicating a win, loss, or draw.
     */
    int simulateRandomPlayout(Node toExplore) {
        // Implementation of the random playout simulation goes here.
        return 0;
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
        int opponent = 3 - playerNo; // we need to determine the opponent first

        List<Board> possibleStates = node.getState().getAllPossibleStates(playerNo);
        possibleStates.forEach(board -> {
            Node newNode = new Node(opponent);  // The new node is from the perspective of the opponent.
            newNode.setState(board);  // Set the board state for the new node.
            node.addChild(newNode);  // Add the new node as a child of the current node.
        });
    }
}