package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;

import ubc.cosc322.core.Board;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

/**
 * Represents the search tree used in the Monte Carlo Tree Search (MCTS) algorithm.
 * This tree structure is essential for navigating and expanding the game state possibilities during the search.
 */
public class Tree {
    private Node root;

    /**
     * Constructs a Tree instance with the specified root node.
     * The root node typically represents the current game state from which the MCTS algorithm begins its exploration.
     *
     * @param root The root node of the tree, representing the initial state for MCTS exploration.
     */
    public Tree(Node root) {
        this.root = root;
    }

    /**
     * Retrieves the root node of the tree.
     * The root node provides the starting point for the MCTS exploration and can be used to navigate through the tree.
     *
     * @return The root node of the tree.
     */
    public Node getRoot() {
        return root;
    }
}
