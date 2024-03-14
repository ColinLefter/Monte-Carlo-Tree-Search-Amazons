package ubc.cosc322;

import ubc.cosc322.core.Board;

import java.util.*;

public class Node {
    private Node parent;
    private List<Node> children;
    private double score;
    private int visitCount;
    private Board state;
    private int playerNo;

    public Node(int playerNo) { // every time we are creating a node, we are tracking all of the following
        this.children = new ArrayList<>();
        this.score = 0;  // Initialize the score for this node
        this.playerNo = playerNo;
    }

    // Getters and setters
    public double getWinScore() {
        return score;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public Node getParent() {
        return parent;
    }


    // Add child nodes
    public void addChild(Node child) {
        children.add(child);
        child.parent = this;
    }
    // Get the state of the board
    public Board getState() {
        return state;
    }
    // Get a list of the children in an array
    public Collection<Node> getChildArray() {
        return new ArrayList<>(children);
    }
    // Get a random child node
    public Node getRandomChildNode() {
        int randomChild = new Random().nextInt(children.size());
        return children.get(randomChild);
    }
    // Return the child with the max score
    public Node getChildWithMaxScore() {
        Node maxScoreNode = null;
        double maxScore = Double.MIN_VALUE;
        for(Node child: children){
            if (child.score > maxScore) {
                maxScore = child.score;
                maxScoreNode = child;
            }
        }
        return maxScoreNode;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getPlayerNo() { // we just need to check what number is present
        return playerNo;
    }

    public void incrementVisit() {
        this.visitCount++;
    }
}
