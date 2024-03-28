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
    final int UPPER_TIME_LIMIT = 5000;
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
        System.out.println("Player Number Debug "+playerNo);
        end = System.currentTimeMillis() + UPPER_TIME_LIMIT;
        Node rootNode = new Node(playerNo);
        rootNode.setState(board);

        // Use a single threaded context to manage the overall time-bound loop.
        while (System.currentTimeMillis() < end) {
            if (rootNode.getState().checkStatus() == Board.IN_PROGRESS) {
                //System.out.println("Debug: Player number " + rootNode.getPlayerNo());
                expandNode(rootNode, rootNode.getPlayerNo());
            }
            //System.out.println("Debug: Child Array of Root Node");
            //System.out.println(rootNode.getChildArray());
            // Check if time has expired before entering another potentially time-consuming operation.
            if (!rootNode.getChildren().isEmpty() && System.currentTimeMillis() < end) {
                // Execute child node processing in parallel, making sure each task is quick and checks time limit.
                rootNode.getChildren().forEach(childNode -> {
                    int playoutResult = simulateRandomPlayout(childNode,playerNo);
                        backPropagation(childNode, playoutResult, playerNo);

                        //System.out.println("Debug: playout result " + playoutResult);
                });
            }
        }
        System.out.println("Score of root node " + rootNode.getScore());
        Node winnerNode = selectPromisingNode(rootNode);

        //Node winnerNode = rootNode.getChildWithMaxScore();
        System.out.println("Debug: Winner node child with highest score");
        System.out.println(winnerNode.getScore());
        System.out.println("Number of children for node: " + rootNode.getChildren().size());
        numberOfNodes = numberOfNodes + (rootNode.getChildren().size());
        if (winnerNode == null) {
            System.out.println("winnerNode = null");
            return board;
        }
        System.out.println("Debug 1.5");
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

    int simulateRandomPlayout(Node currentNode, int playerNo) {
        while (currentNode.getState().checkStatus() == Board.IN_PROGRESS && System.currentTimeMillis() < end) {
            //System.out.println("Debug 1.2");
            // Perform a random move and create a new state
            Board nextBoardState = currentNode.getState().clone();
            System.out.println("Debug: Call RandomPlay");
            nextBoardState.randomPlay(playerNo); // Assuming this method updates the board state

            // Create a new node for this state and link it
            Node childNode = new Node(playerNo);
            childNode.setState(nextBoardState);
            currentNode.addChild(childNode); // Assuming addChild method exists
            childNode.addNodeDepth(currentNode.getNodeDepth());

            // Prepare for the next iteration
            currentNode = childNode; // Move the "focus" to the child node for the next iteration
            playerNo = 3 - playerNo; // Toggle Players
            //System.out.println("Debug: status of current node state - " + childNode.getState().checkStatus());
        }

        int status = currentNode.getState().checkStatus();
        return evaluatePlayoutResult(status, playerNo);
    }

    int evaluatePlayoutResult(int status, int playerNo) {
        //System.out.println("eval play status: status - "+status+" playerNo - "+playerNo);
        if (status == playerNo) {
            return WIN_SCORE;
        } else if (status == 3 - playerNo) {
            return -WIN_SCORE;
        } else {
            return -1;
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
        //while (node != null) {
            node.incrementVisit();
            // Only add score if the playout result corresponds to the node's player winning
            if (node.getPlayerNo() == playerNo && playoutResult == WIN_SCORE) {
                System.out.println("node before add score "+node.getScore());
                node.addScore(WIN_SCORE);
                System.out.println("node after add score "+node.getScore());
                //System.out.println("Debug: Node WINSCORE");
            } else if (node.getPlayerNo() == playerNo && playoutResult == -WIN_SCORE){
                System.out.println("node before minus score "+node.getScore());
                node.addScore(-WIN_SCORE);
                System.out.println("node after minus score "+node.getScore());
                //System.out.println("Debug: Node -WINSCORE");
         //   }
            //node = node.getParent();
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