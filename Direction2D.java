/*
    A "static" class to define various directions.
    Used with the Vector2DInt class
    Directions defined are relevant to Checkers

    @author (Cameron Labelle)
    @version (April 2024)
 */
public class Direction2D {
    // Direction vectors relevant to checkers
    final static Vector2DInt NW = new Vector2DInt(-1, -1);
    final static Vector2DInt NE = new Vector2DInt(1, -1);
    final static Vector2DInt SE = new Vector2DInt(1, 1);
    final static Vector2DInt SW = new Vector2DInt(-1, 1);

    // The directions relevant to different piece movements in Checkers
    static Vector2DInt[] directionsAll = {NW, NE, SE, SW};
    static Vector2DInt[] directionsUp = {NW, NE};
    static Vector2DInt[] directionsDown = {SW, SE};

    public static Vector2DInt[] directionsFromPermission(int permission) {
        // Black moves down, white moves up, kings move any direction
        if (permission == 1)
            return copyAll(directionsDown);
        else if (permission == 2)
            return copyAll(directionsAll);
        return copyAll(directionsUp);
    }

    // Create value copies of a list of Vectors
    private static Vector2DInt[] copyAll(Vector2DInt[] in) {
        Vector2DInt[] copied = new Vector2DInt[in.length];
        for (int i = 0; i < copied.length; i++) {
            copied[i] = Vector2DInt.copy(in[i]);
        }
        return copied;
    }
}
