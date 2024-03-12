package ubc.cosc322.core;

public class Position {
    private int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // For debugging and logging
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
