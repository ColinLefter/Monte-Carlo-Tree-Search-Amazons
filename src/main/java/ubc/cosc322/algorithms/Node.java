package ubc.cosc322.algorithms;

import ubc.cosc322.core.Board;

import java.util.*;

/**
 * Represents a node in the Monte Carlo Tree Search algorithm, encapsulating the game state, player details, and
 * the statistics relevant to MCTS decision-making processes.
 */
public class Node {
    private Node parent;
    private List<Node> children;
    private double score;
    private int visitCount;
    private Board state;
    private int playerNo;
    private double winScore; // We are accounting for wins, losses and draws
    private double drawScore;
    private static final double WIN_SCORE = 1.0; // These are thresholds
    private static final double DRAW_SCORE = 0.5;

    /**
     * Constructs a Node instance for the specified player.
     *
     * @param playerNo The number identifying the player.
     */
    public Node(int playerNo) {
        this.children = new ArrayList<>();
        this.score = 0;
        this.winScore = 10;   // We assume a win to be the best possible condition, hence receiving 10 points.
        this.drawScore = 5;   // A draw is less valuable than a win, but still better than a loss.
        this.state = null;
        this.playerNo = playerNo;
    }

    /**
     * Sets the initial state of the board.
     *
     * @param state the initial state of the board
     */
    public void setState(Board state) {
        this.state = state;
    }

    // Getters and setters
    public double getWinScore() {
        return winScore;
    }

    public double getDrawScore() {
        return drawScore;
    }

    /**
     * Adds the win score to the node's total score, indicating a favorable outcome.
     */
    public void addWinScore() {
        this.score += this.winScore;
    }

    /**
     * Adds the draw score to the node's total score, indicating a neutral outcome.
     */
    public void addDrawScore() {
        this.score += this.drawScore;
    }

    /**
     * Retrieves the visit count for this node.
     *
     * @return The number of times this node has been visited during the simulation.
     */
    public int getVisitCount() {
        return visitCount;
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
     *
     * @param child The child node to add.
     */
    public void addChild(Node child) {
        children.add(child);
        child.parent = this;
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

    /**
     * Selects a random child node from this node's children.
     *
     * @return A randomly selected child node.
     */
    public Node getRandomChildNode() {
        int randomChild = new Random().nextInt(children.size());
        return children.get(randomChild);
    }
    /**
     * Retrieves the child node with the maximum score.
     *
     * @return The child node with the highest score.
     */
    public Node getChildWithMaxScore() {
        return children.stream()
                .max(Comparator.comparingDouble(Node::getWinScore))
                .orElse(null);
    }

    /**
     * Updates the score based on the simulation result.
     *
     * @param result The result from the simulation to update the score accordingly.
     */
    public void updateScore(int result) {
        incrementVisit();
        if (result == WIN_SCORE) {
            addWinScore();
        } else if (result == DRAW_SCORE) {
            addDrawScore();
        }
    }

    /**
     * Sets the parent node for this node.
     *
     * @param node The parent node to set.
     */
    public void setParent(Node node) {
        this.parent = node;
    }

    /**
     * Adds a specified score to this node's total score.
     *
     * @param score The score to add.
     */
    public void addScore(int score) {
        this.score += score;
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
        this.visitCount++;
    }
}