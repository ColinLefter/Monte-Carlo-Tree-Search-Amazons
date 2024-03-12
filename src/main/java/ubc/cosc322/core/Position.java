package ubc.cosc322.core;

/**
 * Represents a position on the game board with x and y coordinates.
 * This class is used to encapsulate the position information, making it easier to manage and pass around positions within the game logic.
 */
public class Position {
    /**
     * The x-coordinate of the position.
     */
    private int x;

    /**
     * The y-coordinate of the position.
     */
    private int y;

    /**
     * Constructs a new Position instance with specified x and y coordinates.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the x-coordinate of the position.
     *
     * @return The x-coordinate of this position.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the y-coordinate of the position.
     *
     * @return The y-coordinate of this position.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns a string representation of the position.
     * This method is useful for debugging and logging purposes, providing a quick and readable representation of the position.
     *
     * @return A string representation of the position in the format "(x, y)".
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
