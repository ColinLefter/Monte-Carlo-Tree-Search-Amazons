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
    public Collection<Object> getChildArray() {
        return new ArrayList<Object>(children);
    }
    // Get a random child node
    public Node getRandomChildNode() {
        int randomChild = new Random().nextInt(children.size());
        return children.get(randomChild);
    }
    // Return the child with the max score
    public Node getChildWithMaxScore() {
        Node maxScoreNode = null;
        double maxScore = -9999;
        for(Node child: children){
            if(child.winScore>maxScore){
                maxScore = child.winScore;
                maxScoreNode = child;
            }
        }
        return maxScoreNode;
    }
}
