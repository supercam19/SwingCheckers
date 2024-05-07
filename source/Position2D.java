/*
    A 2-dimensional position within a JFrame. Useful for storing an x position
    and a y position in a single variable.
    Also, helpful for comparing two positions (.equals())

    @author (Cameron Labelle)
    @version (April 2024)
 */
public class Position2D {
    int x;
    int y;

    // Default a position to 0, 0 if none is declared
    public Position2D() {
        x = 0;
        y = 0;
    }

    // Constructor with x, y position specified
    public Position2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Update x, y position
    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Compare this Position2D with another Position2D and return if they're the same
    public boolean equals(Position2D other) {
        return x == other.x && y == other.y;
    }
}
