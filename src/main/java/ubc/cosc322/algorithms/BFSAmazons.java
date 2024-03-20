package ubc.cosc322.algorithms;

import java.util.*;

import ubc.cosc322.core.Board;

public class BFSAmazons {
    
    // Used to speed counts up if finished.
    public int totalBlackCount = 0; // Player 1
    public int totalWhiteCount = 0; // Player 2

    Deque<BFSNode> queue; // Queue of nodes to be searched. Cannot contain arrows.
    HashSet<BFSNode> visitedNodes; // Nodes already searched.
    Deque<BFSNode> whiteQueens; // White queens found in the same partition as another white queen.
    Deque<BFSNode> blackQueens; // Black queens found in the same partition as another black queen.

    public BFSAmazons() {
        // Initialize array
        queue = new ArrayDeque<>();
        visitedNodes = new HashSet<>();
        whiteQueens = new ArrayDeque<>();
        blackQueens = new ArrayDeque<>();
    }

    // Returns state of the board.
    // 0 if partition in progress, 1 if finished.
    public int searchBoardPartition(int[][] board, int x, int y, int color) {
        // 1. Check that the initial queen is not completely blocked. If it is, the partition is complete.
        // 2. Check if there is an adjacent queen of the same color. If it is, add to list and continue search.
        // 3. Check if there is an adjacent queen of a different color. If it is, check if it is blocked.
        //    If it is blocked, continue search. Otherwise, return in progress.

        // System.out.println("NEW SEARCH");

        // Variables
        BFSNode currentNode; // 
        int tileValue, tileValue2;
        int temporaryCount = 0; // initial queen position does not count.

        // If a queen of the same color was found in a previous partition, then the partition has already been searched.
        if (color == 1) {
            if (blackQueens.contains(new BFSNode(x, y, color)))
                return 1;
        } else {
            if (whiteQueens.contains(new BFSNode(x, y, color)))
                return 1;
        }
        
        // Search begins here
        queue.addLast(new BFSNode(x, y, color)); // Initial queen node.

        /* QUEEN ADJACENT SEARCH */
        // Do an initial check on the first node (queen), different rules than free space
        currentNode = queue.removeFirst();

        boolean isBlocked = true;
        for (int i = -1; i < 2; i++) { // -1, 0, 1
            for (int j = -1; j < 2; j++) { // -1, 0, 1

                if (i != 0 || j != 0) { // Skip the current node, where x=x and y=y

                    // Only consider nodes on the board.
                    if((currentNode.x+i) > -1 && (currentNode.x+i) < Board.DEFAULT_BOARD_SIZE 
                    && (currentNode.y+j) > -1 && (currentNode.y+j) < Board.DEFAULT_BOARD_SIZE) {

                        // Check for complete blockage
                        if (board[currentNode.x+i][currentNode.y+j] == 0) {
                            isBlocked = false;
                        }

                    }
                }
            }
        }

        if (isBlocked) {
            // System.out.println("IS BLOCKED");
            return 1; // Partition finished.
        }

        /* ADJACENT SEARCH, QUEEN NOT BLOCKED */
        for (int i = -1; i < 2; i++) { // -1, 0, 1
            for (int j = -1; j < 2; j++) { // -1, 0, 1

                if (i != 0 || j != 0) { // Skip the current node, where x=x and y=y

                    // Only consider nodes on the board.
                    if((currentNode.x+i) > -1 && (currentNode.x+i) < Board.DEFAULT_BOARD_SIZE 
                    && (currentNode.y+j) > -1 && (currentNode.y+j) < Board.DEFAULT_BOARD_SIZE) { // Skip nodes outside of board
                        
                        // Get the tile value of the adjacent node
                        tileValue = board[currentNode.x+i][currentNode.y+j];

                        // If the tile value is not a free space
                        if (tileValue != 0 && tileValue != 3) {

                            // If there is a queen of the same color, add to list and continue search.
                            if (tileValue == color) {

                                if (color == 1) {
                                    blackQueens.add(currentNode);
                                } else {
                                    whiteQueens.add(currentNode);
                                }

                            // If there is a queen of a different color, if it is not blocked, return in progress.
                            } else {

                                // Check for trapped opposing queen
                                for (int k = -1; k < 2; k++) { // -1, 0 , -1
                                    for (int l = -1; l < 2; l++) { // -1, 0 , -1
                                        if (k != 0 || l != 0) {
                                            if( currentNode.x+i+k >= 0 && currentNode.x+i+k < Board.DEFAULT_BOARD_SIZE 
                                            && currentNode.y+j+l >= 0 && currentNode.y+j+l < Board.DEFAULT_BOARD_SIZE) {
                                                tileValue2 = board[currentNode.x+i+k][currentNode.y+j+l];
                                                if (tileValue2 == 0) {
                                                    return 0; // other queen can move, so in progress.
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                        // If the tile is a free space
                        } else if (tileValue == 0){
                            // Check that we havent seen this node already
                            BFSNode checkNode = new BFSNode(currentNode.x+i, currentNode.y+j, color);
                            if (!visitedNodes.contains(checkNode)) {
                                queue.addLast(checkNode);
                                visitedNodes.add(checkNode);
                                temporaryCount++;
                            }
                        }
                    }
                }
            }
        }
        
        /* REMAINING PARTITION SEARCH  */
        int iter = 0;
        while(!queue.isEmpty()) { // Continue as long as free spaces are found
            currentNode = queue.removeFirst();
            visitedNodes.add(currentNode);
            iter++;
            if (iter > 500)
                return -1;

            for (int i = -1; i < 2; i++) { // -1, 0, -1
                for (int j = -1; j < 2; j++) { // -1, 0, -1

                    if (i != 0 || j != 0) { // Skip the current node, x=x, y=y

                        if(currentNode.x+i >= 0 && currentNode.x+i < Board.DEFAULT_BOARD_SIZE 
                        && currentNode.y+j >= 0 && currentNode.y+j < Board.DEFAULT_BOARD_SIZE) { // Skip nodes outside of board
                        
                            // Get the tile value of the adjacent node
                            tileValue = board[currentNode.x+i][currentNode.y+j];
                            // If the tile value is not a free space
                            if (tileValue != 0 && tileValue != 3) {
                                
                                // Return if we find a queen of a different color
                                if(tileValue != color) {
                                    return 0;
                                
                                // Found another queen in same partition. Continue, add found queen to queens list.
                                } else if (tileValue == color) {

                                    // Don't return, but add to list of queens
                                    if (color == 1) {
                                        blackQueens.add(currentNode);
                                    } else {
                                        whiteQueens.add(currentNode);
                                    }
                                }

                            // If the tile is a free space
                            } else if (tileValue == 0) {
                                // Check that we havent seen this node already
                                BFSNode checkNode = new BFSNode((currentNode.x+i), (currentNode.y+j), color);
                                if (!visitedNodes.contains(checkNode)) {
                                    queue.addLast(checkNode);
                                    visitedNodes.add(checkNode);
                                    temporaryCount++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (color == 1)
            totalBlackCount += temporaryCount;
        else
            totalWhiteCount += temporaryCount;
        return 1;
    }

    // For testing, disregard
    public boolean verifyEquality() {
        BFSNode n1 = new BFSNode(3, 4, 1);
        BFSNode n2 = new BFSNode(3, 4, 1);

        Deque<BFSNode> nd = new ArrayDeque<>();
        // Set<BFSNode> ns = new HashSet<>();
        nd.add(n1);

        return nd.contains(n2);
    }

}

// Helper class
class BFSNode {
    public int x;
    public int y;
    public int color;

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

        boolean result = (this.x == node.x && this.y == node.y);

        return result;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
}
