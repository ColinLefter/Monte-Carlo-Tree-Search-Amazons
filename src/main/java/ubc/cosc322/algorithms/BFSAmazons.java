package ubc.cosc322.algorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ubc.cosc322.core.Board;

// This is a bit much. Ask Jar(o)d :)
public class BFSAmazons {

    // Used to speed counts up if finished.
    public int totalBlackCount = 0; // Player 1
    public int totalWhiteCount = 0; // Player 2

    // Queue holds unsearched nodes, set holds searched nodes
    LinkedList<BFSNode> queue;
    Set<BFSNode> searchedNodes;

    public BFSAmazons() {
        queue = new LinkedList<>();
        searchedNodes = new HashSet<>();
    }

    // Returns state of the board. 0 if partition in progress, 1 if finished.
    public int searchBoardPartition(int[][] board, int x, int y, int color) {
        // Reset counts before starting a new search
        totalBlackCount = 0;
        totalWhiteCount = 0;

        // Reset the searchedNodes and queue as well
        searchedNodes.clear();
        queue.clear();

        // Initialize the search with the starting node
        BFSNode startNode = new BFSNode(x, y, color);
        queue.addLast(startNode);
        searchedNodes.add(startNode);

        boolean adjacentOpponentQueenFound = false; // Flag to mark the presence of an adjacent opponent queen
        int tileValue;

        while (!queue.isEmpty()) {
            BFSNode currentNode = queue.removeFirst();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue; // Skip the current node
                    int nextX = currentNode.x + i;
                    int nextY = currentNode.y + j;

                    if (nextX >= 0 && nextX < Board.DEFAULT_BOARD_SIZE && nextY >= 0 && nextY < Board.DEFAULT_BOARD_SIZE) {
                        tileValue = board[nextX][nextY];
                        BFSNode nextNode = new BFSNode(nextX, nextY, color);

                        if (tileValue == 0 && !searchedNodes.contains(nextNode)) {
                            // If the tile is empty and we haven't already searched this node, add it to the queue
                            queue.addLast(nextNode);
                            searchedNodes.add(nextNode);
                        } else if (tileValue != color && tileValue != 3) {
                            // If we find an opponent's queen adjacent to the start node, mark it
                            adjacentOpponentQueenFound = true;
                        }
                    }
                }
            }
        }

        // Update counts after search
        if (color == 1) {
            totalBlackCount += searchedNodes.stream().filter(node -> node.color == color).count();
        } else {
            totalWhiteCount += searchedNodes.stream().filter(node -> node.color == color).count();
        }

        // Determine the game status based on the search outcome
        if (adjacentOpponentQueenFound) {
            // If an adjacent opponent queen was found, the game might still be in progress
            // The actual game status would depend on the ability of any queen to move, which should be evaluated separately
            return 0;
        } else {
            // If no adjacent opponent queen was found, evaluate the board based on the occupied territory
            return evaluateBoardStatus();
        }
    }

    public int evaluateBoardStatus() {
        // Assuming you have a method to evaluate board status based on the current state of counts
        // This method should return DRAW, P1, or P2 based on the territories controlled by each player
        if (totalBlackCount > totalWhiteCount) {
            return Board.P1;
        } else if (totalWhiteCount > totalBlackCount) {
            return Board.P2;
        } else {
            return Board.DRAW;
        }
    }


    // Helper class
    class BFSNode {
        int x;
        int y;
        int color;

        BFSNode(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BFSNode node = (BFSNode) o;
            return this.x == node.x && this.y == node.y && this.color == node.color;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + x;
            result = 31 * result + y;
            result = 31 * result + color;
            return result;
        }

    }
}
