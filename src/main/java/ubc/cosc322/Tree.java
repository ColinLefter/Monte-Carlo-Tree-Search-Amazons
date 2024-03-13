package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;

import ubc.cosc322.core.Board;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

public class Tree {
    private Node root;

    public Tree(Board initialState) {
        root = new Node(initialState);
    }

    // Method to get the root node
    public Node getRoot() {
        return root;
    }

    public void setRoot(Node winnerNode) {
        root = winnerNode;
    }

    // You might want to add methods to manipulate the tree if needed
}

