package ubc.cosc322.algorithms;

import ubc.cosc322.core.Board;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a node in the Monte Carlo Tree Search algorithm, encapsulating the game state, player details, and
 * the statistics relevant to MCTS decision-making processes.
 */
public class Node {
    private Node parent;
    private List<Node> children;
    private Board state;
    private int playerNo;
    private int nodeDepth;
    private AtomicInteger visitCount = new AtomicInteger(0);
    private AtomicInteger score = new AtomicInteger(0);

    /**
     * Constructs a Node instance for the specified player.
     *
     * @param playerNo The number identifying the player.
     */
    public Node(int playerNo) {
        this.children = new ArrayList<>();
        this.playerNo = playerNo;
        this.nodeDepth = 0;
    }


    /**
     * Sets the initial state of the board.
     *
     * @param state the initial state of the board
     */
    public void setState(Board state) {
        this.state = state;
    }

    /**
     * Adds the win score to the node's total score, indicating a favorable outcome.
     */
    public void addScore(int scoreToAdd) {
        this.score.addAndGet(scoreToAdd);
    }

    /**
     * Retrieves the visit count for this node.
     *
     * @return The number of times this node has been visited during the simulation.
     */
    public int getVisitCount() {
        return visitCount.get();
    }

    /**
     * Retrieves the parent of this node in the search tree.
     *
     * @return The parent node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Adds a child node to this node.
     * Increases depth counter of node by 1.
     *
     * @param child The child node to add.
     */
    public void addChild(Node child) {
        children.add(child);
        child.parent = this;
        child.nodeDepth = this.nodeDepth + 1;
    }
    /**
     * Retrieves the game state associated with this node.
     *
     * @return The game state.
     */
    public Board getState() {
        return state;
    }

    public List<Node> getChildren() {
        return children;
    }

    /**
     * Retrieves a collection of this node's children.
     *
     * @return A collection of child nodes.
     */
    public Collection<Node> getChildArray() {
        return new ArrayList<>(children);
    }


    public double getScore() {
        return score.get();
    }

    /**
     * Retrieves the player number associated with this node.
     *
     * @return The player number.
     */
    public int getPlayerNo() { // we just need to check what number is present
        return playerNo;
    }

    /**
     * Increments the visit count for this node by one.
     */
    public void incrementVisit() {
        this.visitCount.incrementAndGet();
    }

    public int getNodeDepth() {
        return nodeDepth;
    }
    public void setNodeDepth(int nodeDepth) {
        this.nodeDepth = nodeDepth;
    }
}
