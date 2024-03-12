package ubc.cosc322;

import java.util.*;

import sfs2x.client.entities.Room;
import ubc.cosc322.core.Board;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.GameMessage;

public class MonteCarloTreeSearch {
    /*
    4 steps: Selection, expansion, simulation and backpropagation
    We need to iteratively perform these steps until we exhaust our computation budget (30s time limit).
    Then we need to choose the move that has the highest win ratio from the root node

    1. Selection:
    - Start from the root node and select successive child nodes until a leaf node is reached
    - The child is selected based on maximizing the Upper Confidence bound applied to Trees (UCT)
    2. Expansion:
    - Unless the leaf node ends the game with a win/loss for either player, create one or more child nodes and choose one to explore
    3. Simulation:
    - From the new node, simulate a random playout until the game reaches a terminal state (win/loss/draw)
    4. Backpropagation:
    - Update the information in the nodes from the played-out node up to the root node based on the result of the playout
    */
    List<List<Integer>> blackPositions = new ArrayList<>();
    List<List<Integer>> whitePositions = new ArrayList<>();
    final String OPPONENT = "white";
    static final int WIN_SCORE = 10;
    int level;
    int opponent;

    public MonteCarloTreeSearch() {
        initializePositions();
    }

    public void initializePositions() {
        // Initializing black positions
        blackPositions.add(Arrays.asList(1, 7));
        blackPositions.add(Arrays.asList(4, 10));
        blackPositions.add(Arrays.asList(7, 10));
        blackPositions.add(Arrays.asList(10, 7));

        // Initializing white positions
        whitePositions.add(Arrays.asList(1, 4));
        whitePositions.add(Arrays.asList(4, 1));
        whitePositions.add(Arrays.asList(7, 1));
        whitePositions.add(Arrays.asList(10, 4));
    }
    public void sendMoveMessage(java.util.ArrayList<java.lang.Integer> queenPosCurrent,
                                java.util.ArrayList<java.lang.Integer> queenPosNew,
                                java.util.ArrayList<java.lang.Integer> arrowPos) {

    }
    public Board findNextMove(Board board, int playerNo) {
        // give 30 seconds to choose the right node
        long end = System.currentTimeMillis() + 30000;

        opponent = 3 - playerNo;
        Tree tree = new Tree(board);
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(opponent);

        while (System.currentTimeMillis() < end) {
            Node promisingNode = selectPromisingNode(rootNode);
            if (promisingNode.getState().getBoard().checkStatus()
                    == Board.IN_PROGRESS) {
                expandNode(promisingNode);
            }
            Node nodeToExplore = promisingNode;
            if (!promisingNode.getChildArray().isEmpty()) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getBoard();
    }


}
