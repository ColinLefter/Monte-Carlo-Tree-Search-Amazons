package ubc.cosc322.algorithms;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ubc.cosc322.core.Board;

/**
 * Implements the Monte Carlo Tree Search (MCTS) algorithm for the Game of the Amazons.
 * The class iteratively builds a game tree, evaluates the possible moves using simulations, and selects the best move based on the simulation outcomes.
 */
public class MonteCarloTreeSearch {
    // Lists to hold the positions of black and white queens on the board.
    List<List<Integer>> blackPositions = new ArrayList<>();
    List<List<Integer>> whitePositions = new ArrayList<>();
    static final int WIN_SCORE = 10; // Score indicating a win in simulations.
    final int UPPER_TIME_LIMIT = 2000;
    public static int numberOfNodes = 0;
    long end;

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
        end = System.currentTimeMillis() + UPPER_TIME_LIMIT;
        Node rootNode = new Node(playerNo);
        rootNode.setState(board);
        expandNode(rootNode, rootNode.getPlayerNo());
        // Use a single threaded context to manage the overall time-bound loop.
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (System.currentTimeMillis() < end) {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (Node childNode : rootNode.getChildren()) {
                Callable<Void> task = () -> {
                    simulateRandomPlayout(childNode, playerNo);
                    return null;
                };
                tasks.add(task);
            }
            try {
                executor.invokeAll(tasks); // Executes in parallel
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        executor.shutdown();
        System.out.println("Games played: "+Board.gamesPlayed);
        //System.out.println("Score of root node " + rootNode.getScore());
        Node winnerNode = selectPromisingNode(rootNode);

        //Node winnerNode = rootNode.getChildWithMaxScore();
        System.out.println("Winner node child with highest score: "+winnerNode.getScore());
        numberOfNodes = numberOfNodes + (rootNode.getChildren().size());
        if (winnerNode == null) {
            System.out.println("winnerNode = null");
            return board;
        }
        System.out.println("Winner node found.");
        //Board.printBoard(winnerNode.getState().getBoard());
        return winnerNode.getState();
    }

    /**
     * Selects the most promising node to explore based on the UCT value.
     *
     * @param rootNode The node from which to select the promising node.
     * @return The selected promising node.
     */
    public Node selectPromisingNode(Node rootNode) {
        //System.out.println("selectpromisingnode activated");
        //node with the highest amount of playouts is returned
        Node node;
        node = UCT.findBestNodeWithUCT(rootNode);
        return node;
    }

    private void simulateRandomPlayout(Node currentNode, int playerNo) {
        int counter = 0;
        while (currentNode.getState().checkStatus() == Board.IN_PROGRESS && System.currentTimeMillis() < end) {
            //System.out.println("Debug 1.2");
            // Perform a random move and create a new state
            Board nextBoardState = currentNode.getState().clone();
            nextBoardState.randomPlay(playerNo); // Assuming this method updates the board state

            // Create a new node for this state and link it
            Node childNode = new Node(playerNo);
            childNode.setState(nextBoardState);
            synchronized (currentNode) {
                //Following line prints out boards synchronized so we can see the states
                //Board.printSynchronizedBoard(childNode.getState());
                currentNode.addChild(childNode);
            }
            childNode.setNodeDepth(currentNode.getNodeDepth()+1);

            // Prepare for the next iteration
            currentNode = childNode; // Move the "focus" to the child node for the next iteration
            playerNo = 3 - playerNo; // Toggle Players
            counter++;
            //System.out.println("Debug: status of current node state - " + childNode.getState().checkStatus());
        }
        int status = currentNode.getState().checkStatus();
//        int result = evaluatePlayoutResult(status);
        backPropagation(currentNode, status);
    }

    /**
     * Backpropagates the result of the simulation up the tree, updating the statistics of the nodes.
     *
     * @param node The node from which to start backpropagation.
     * @param status The result of the playout to be backpropagated.
     */
    public void backPropagation(Node node, int status) {
        //System.out.println("activate back propagation");
        final int finalDepth = 0;
        while (node != null && node.getNodeDepth() != finalDepth) {
            node.incrementVisit();
            // Only add score if the playout result corresponds to the node's player winning
            if (status == Board.getCurrentPlayer()) {
                //System.out.println("node before add score " + node.getScore());
                node.addScore(WIN_SCORE);
                //System.out.println("Debug: Node WINSCORE");
            } else if (status == (3 - (Board.getCurrentPlayer()))) {
                //System.out.println("node before minus score " + node.getScore());
                node.addScore(-WIN_SCORE);
            } else if (status == 0) {
                node.addScore(-WIN_SCORE);
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
            Node childNode = new Node(playerNo); // The player for the next turn.
            childNode.setState(state);
            node.addChild(childNode);
        });
    }
}