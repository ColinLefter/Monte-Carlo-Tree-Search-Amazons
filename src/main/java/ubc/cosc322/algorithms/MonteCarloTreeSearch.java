package ubc.cosc322.algorithms;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import ubc.cosc322.core.Board;
import ubc.cosc322.core.Position;

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
    final int UPPER_TIME_LIMIT = 10000;
    public static int numberOfNodes = 0;

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
        Node rootNode = new Node(playerNo);
        rootNode.setState(board);

        // Use a single threaded context to manage the overall time-bound loop.
        while (System.currentTimeMillis() < end) {
            Node promisingNode = selectPromisingNode(rootNode);
            //System.out.println("bug test 1.3");

            if (promisingNode.getState().checkStatus() == Board.IN_PROGRESS) {
                expandNode(promisingNode, 3 - promisingNode.getPlayerNo());
                //System.out.println("bug test 1.6");
            }

            // Check if time has expired before entering another potentially time-consuming operation.
            if (!promisingNode.getChildren().isEmpty() && System.currentTimeMillis() < end) {
                //System.out.println("bug test 1.7");

                // Execute child node processing in parallel, making sure each task is quick and checks time limit.
                promisingNode.getChildren().parallelStream().forEach(childNode -> {
                    if (System.currentTimeMillis() < end) {
                        int playoutResult = simulateRandomPlayout(childNode);
                        synchronized (rootNode) {
                            backPropagation(childNode, playoutResult, playerNo);
                        }
                        //System.out.println("bug test 1.8");
                    }
                });
            }
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        System.out.println("Number of children for node: " + rootNode.getChildren().size());
        numberOfNodes = numberOfNodes + (rootNode.getChildren().size());
        if (winnerNode == null) {
            System.out.println("winnerNode = null");
            return board;
        }
        System.out.println("Winner node found.");
        return winnerNode.getState();
    }

    /**
     * Selects the most promising node to explore based on the UCT value.
     *
     * @param node The node from which to select the promising node.
     * @return The selected promising node.
     */
    public Node selectPromisingNode(Node node) {
        //System.out.println("selectpromisingnode activated");
        //node with the highest amount of playouts is returned
        Node promisingNode = node;
        while (!promisingNode.getChildArray().isEmpty()) {     //while there are still children left to explore
            promisingNode = UCT.findBestNodeWithUCT(promisingNode);
        }
        return promisingNode;
    }

    int simulateRandomPlayout(Node toExplore) {
        Node tempNode = new Node(toExplore.getPlayerNo());
        tempNode.setState(toExplore.getState().clone());
        Board tempBoard = tempNode.getState();

        Set<Board> visitedStates = new HashSet<>(); // Track visited states for cycle detection
        int maxDepth = 50; // Limit simulation depth to prevent infinite loops

        for (int depth = 0; depth < maxDepth && tempBoard.checkStatus() == Board.IN_PROGRESS; depth++) {
            if (!visitedStates.add(tempBoard.clone())) {
                // Cycle detected, break the simulation
                break;
            }
            tempBoard.randomPlay(); // A random play on a temporary board is valid as this is a simulation
        }

        int status = tempBoard.checkStatus();
        if (status == toExplore.getPlayerNo()) {
            // The initiating player wins
            return WIN_SCORE;
        } else if (status == Board.DRAW) {
            // The game ends in a draw
            return 0; // Assuming a draw is considered neutral and given a score of 0
        } else if (status == tempBoard.getOpponent(toExplore.getPlayerNo())) {
            // The initiating player loses
            return -WIN_SCORE;
        } else {
            // If the game is still in progress
            return 0;
        }
    }


    /**
     * Backpropagates the result of the simulation up the tree, updating the statistics of the nodes.
     *
     * @param node The node from which to start backpropagation.
     * @param playoutResult The result of the playout to be backpropagated.
     * @param playerNo The player number associated with each node
     */
    public void backPropagation(Node node, int playoutResult, int playerNo) {
        //System.out.println("activate back propagation");
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
     * Expands the given node by creating new child nodes that represent all possible future game states
     * arising from the current state. This method leverages parallel processing to concurrently evaluate
     * different potential game states and associated moves, enhancing the computational efficiency.
     * The expansion considers all possible movements for each queen followed by all potential arrow shots
     * for those moves, encapsulating the breadth of possible game progressions from the current state.
     *
     * Note: The method synchronizes access to the node's children to safely add new child nodes in a
     * multithreaded environment, preventing concurrent modification issues.
     *
     * @param node The node to be expanded, representing the current game state.
     * @param playerNo The player number (1 or 2) for whom the expansion is being done.
     */

    private void expandNode(Node node, int playerNo) {
        // Use the states generated by getAllPossibleStates, which include both queen moves and arrow shots.
        node.getState().getAllPossibleStates(playerNo).forEach(state -> {
            Node childNode = new Node(3 - playerNo); // The player for the next turn.
            childNode.setState(state);
            synchronized (node) {
                node.addChild(childNode);
            }
        });
    }
}