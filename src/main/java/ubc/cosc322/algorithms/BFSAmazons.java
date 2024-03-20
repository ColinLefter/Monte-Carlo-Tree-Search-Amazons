package ubc.cosc322.algorithms;

import java.util.*;

import ubc.cosc322.core.Board;

// This is a bit much. Ask Jar(o)d :)
public class BFSAmazons {
    
    // Used to speed counts up if finished.
    public int totalBlackCount = 0; // Player 1
    public int totalWhiteCount = 0; // Player 2

    // Queue holds unsearched nodes, set holds searched nodes
    Deque<BFSNode> queue;
    Set<BFSNode> searchedNodes;

    public BFSAmazons() {
        queue = new ArrayDeque<>();
        searchedNodes = new HashSet<>();
    }

    // Returns state of the board. 0 if partition in progress, 1 if finished.
    public int searchBoardPartition(int[][] board, int x, int y, int color) {
        queue.addLast(new BFSNode(x, y, color)); // Queen node

        BFSNode currentNode;
        boolean firstNode = true, adjacentQueen = false;
        int tileValue;
        int temporaryCount = 0, lastCount; // initial queen position does not count.
        while(!queue.isEmpty()) { // Add to queue every time more nodes are found
            currentNode = queue.removeFirst();

            lastCount = temporaryCount;
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {

                    if (i != 0 && j != 0 && currentNode.x+i >= 0 && currentNode.x+i < Board.DEFAULT_BOARD_SIZE && currentNode.y+j >= 0 && currentNode.y+j < Board.DEFAULT_BOARD_SIZE) { // Skip the current node, and nodes outside the board
                        
                        tileValue = board[currentNode.x+i][currentNode.y+j];
                        if (tileValue != 0) {
                            // Return if we find a queen of a different color
                            if(tileValue != color && tileValue != 3) {
                                if (firstNode) {
                                    adjacentQueen = true;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            // Check that we havent seen this node already
                            BFSNode checkNode = new BFSNode(currentNode.x+i, currentNode.y+j, color);
                            if (!searchedNodes.contains(checkNode) && !queue.contains(checkNode)) {
                                queue.addLast(checkNode);
                                temporaryCount++;
                            }
                        }
                    }
                }
            }
            firstNode = false;
            if(adjacentQueen && lastCount == temporaryCount) {
                return 1; // We found that there is another queen adjacent to ours, and there are no available moves. 
            }
        }

        if (color == 1)
            totalBlackCount += temporaryCount;
        else
            totalWhiteCount += temporaryCount;

        return 1;
    }

    public int getOutcome() {
        if (totalWhiteCount == totalBlackCount) {
            return Board.DRAW;
        } else if(totalBlackCount > totalWhiteCount) {
            return Board.P1;
        } else {
            return Board.P2;
        }
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
        return this.x == node.x && this.y == node.y &&this.color == node.color;
    }
}
