/*
    A class that represents movement with the idea of a vector.
    2D : 2-dimensional vector
    Int : Movement is discrete

    @author (Cameron Labelle)
    @version (April 2024)
 */
public class Vector2DInt {
    int x;
    int y;
    public Vector2DInt(int dx, int dy) {
        x = dx;
        y = dy;
    }

    // Take one step further in the vector's direction
    public void step() {
        if (x < 0)
            x--;
        else if (x > 0)
            x++;
        if (y < 0)
            y--;
        else if (y > 0)
            y++;
    }

    // Reduce the vector to +/-(1, 1), maintaining direction
    public void normalize() {
        if (x < 0)
            x = -1;
        else if (x > 0)
            x = 1;
        if (y < 0)
            y = -1;
        else if (y > 0)
            y = 1;
    }

    // Comparing method for 2 vectors
    public boolean equals(Vector2DInt v) {
        return x == v.x && y == v.y;
    }

    // Generate a vector from 2 positions (start, end)
    public static Vector2DInt generateVector(Position2D p1, Position2D p2) {
        return new Vector2DInt(p2.x - p1.x, p2.y - p1.y);
    }

    // Return a copy of the passed vector
    public static Vector2DInt copy(Vector2DInt src) {
        return new Vector2DInt(src.x, src.y);
    }
}


