package ubc.cosc322;

import ubc.cosc322.core.Board;

import java.util.*;

public class Node {
    private Node parent;
    private List<Node> children;
    private double winScore;
    private int visitCount;
    private Board state;

    public Node(Board state) {
        this.state = state;
        this.children = new ArrayList<>();
    }

    // Getters and setters
    public double getWinScore() {
        return winScore;
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
            if (child.winScore>maxScore){
                maxScore = child.winScore;
                maxScoreNode = child;
            }
        }
        return maxScoreNode;
    }
    // Set the node of the parent to this node
    public void setParent(Node node) {
        this.parent = node;
    }
}
