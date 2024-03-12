package ubc.cosc322.core.actionFactory;

public class Action {
    // TODO: Verify that this is the proper format as compared to server
    int oldX, oldY, newX, newY; // Based on board, x: 0-9, y: 0-9, top-left to bottom-right

    Action(int oldX, int oldY, int newX, int newY) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

    public void displayMove(int i) {
        System.out.println();
        System.out.println("Move #" + i);
        System.out.println("Old Position row: " + oldX + " col: " + oldY);
        System.out.println("New Position row: " + newX + " col: " + newY);
    }
}
