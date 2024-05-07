/*
    A class that represents one tile or grid on a checkers board.
    Inherits from Position2D for its position in the JFrame.
    Stores pieces as an instance variable, rather than having them inherit.

    @author (Cameron Labelle)
    @version (April 2024)
 */
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Tile extends Position2D{
    final int TILE_SIZE = 60; // pixels
    boolean playable;
    boolean holdsPiece = false;
    CheckersPiece piece;

    // All possible colors for tiles (playable tiles, non-playable tiles, and highlighted tiles)
    static Color playableTile = new Color(27, 135, 56);
    static Color nonPlayableTile = new Color(219, 175, 92);
    static Color playableHighlight = new Color(28, 255, 51);
    // Colors for depth effect on tiles
    static Color playableDarker = new Color(16, 97, 38);
    static Color nonPlayableDarker = new Color(176, 139, 70);
    // The dimming effect applied to tiles (uses semi-transparent square)
    static Color ignoreByCapture = new Color(0, 0, 0, 100);
    // Store active color for this tile
    Color myColor;
    Color borderColor;
    BasicStroke borderSize;
    boolean highlighted;
    // Is the dimming effect applied?
    boolean dimmed = false;
    final int BORDER_PX = 2; // DO NOT change unless you alter draw() method

    // Constructor; pass position and whether it is a playable tile
    public Tile(int xPos, int yPos, boolean playable) {
        // Call Position constructor
        super(xPos, yPos);
        this.playable = playable;
        // Determine tile colors based on whether its playable
        if (this.playable) {
            myColor = playableTile;
            borderColor = playableDarker;
        }
        else {
            myColor = nonPlayableTile;
            borderColor = nonPlayableDarker;
        }
        borderSize = new BasicStroke(BORDER_PX);
    }

    // Draw the tile (square on board) and if it holds a piece, draw that too
    public void draw(Graphics gr) {
        Graphics2D g = (Graphics2D)gr;
        g.setColor(myColor);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        // Call the draw() on CheckersPiece if this tile has one
        if (holdsPiece)
            piece.draw(x, y, g);
        // Highlighted tiles don't have borders (depth effect) or dim effect applied to them
        if (!highlighted) {
            g.setColor(borderColor);
            g.setStroke(borderSize);
            // borderSize is 2 by default so offset into the tile by one pixel on each side
            g.drawRect(x + 1, y + 1, TILE_SIZE - 2,  TILE_SIZE - 2);
            if (dimmed) {
                g.setColor(ignoreByCapture);
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    // Take the piece from this tile and return it (technically a value copy of it)
    public CheckersPiece takePiece() {
        if (holdsPiece) {
            // Copy piece and important values not defined in constructor
            CheckersPiece copy = new CheckersPiece(piece.color == Color.BLACK);
            copy.moves = piece.moves;
            copy.crowned = piece.crowned;
            copy.symbol = piece.symbol;
            // Remove the piece stored in this Tile
            piece = null;
            holdsPiece = false;
            return copy;
        }
        // Hope this doesn't happen
        else
            return null;
    }

    // Apply highlight effect (set color to bright green)
    public void setHighlight(boolean enable) {
        highlighted = enable;
        if (enable)
            myColor = playableHighlight;
        else
            myColor = playableTile;
    }

    // Set the CheckersPiece held in this Tile
    public void setPiece(CheckersPiece newPiece) {
        holdsPiece = true;
        piece = newPiece;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
