package ubc.cosc322;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

import sfs2x.client.entities.Room;
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
    public void sendMoveMessage(java.util.ArrayList<java.lang.Integer> queenPosCurrent,
                                java.util.ArrayList<java.lang.Integer> queenPosNew,
                                java.util.ArrayList<java.lang.Integer> arrowPos) {

    }
}
