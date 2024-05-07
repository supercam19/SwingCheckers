/*
    This class represents a single checkers game piece.
    Store its graphical representation as a unicode character


    @author (Cameron Labelle)
    @version (April 2024)
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class CheckersPiece{
    final char CHECKERS_MAN = '\u26C2'; // \u26C2
    final char CHECKERS_KING = '\u26C3'; // \u26C3
    char symbol = CHECKERS_MAN;
    Color color;
    boolean crowned = false;
    boolean black;
    final int PIECE_SIZE = 50;
    final int TILE_SIZE = 60;

    ArrayList<Tile> moves = new ArrayList<Tile>();

    // Constructor; pass true to make piece black, false to be white.
    public CheckersPiece(boolean black) {
        this.black = black;
        if (black)
            color = Color.BLACK;
        else
            color = Color.WHITE;
    }

    // Call this method to make the piece a king when it reaches the other side of the board
    public void crown() {
        crowned = true;
        symbol = CHECKERS_KING;
    }

    // Draw the checkers piece at an x, y position.
    public void draw(int x, int y, Graphics g) {
        g.setColor(color);
        // Create the new font to draw the symbol
        g.setFont(new Font("Monospaced", Font.PLAIN, PIECE_SIZE));
        // Convert to string so that we can use drawString()
        String glyph = ""+symbol;

        /*
         * Our Unicode Symbols are slightly different sizes.
         * So we want to bound them inside and centered to our
         * square space. Code for FontMetrics borrowed and
         * modified sligtly to apply to our current object size
         * -StackOverFlow "Java-Center-Text-In-Rectangle"
         * Accessed 2024/03/20
         */
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(glyph, g);
        int pX = (TILE_SIZE - (int) r.getWidth()) / 2;
        int pY = (TILE_SIZE - (int) r.getHeight()) / 2 + fm.getAscent();

        g.drawString(glyph, x+pX, y+pY);
    }

    // Draw the piece centered at an x, y position (used for piece dragging effect)
    public void drawCenteredAt(int x, int y, Graphics g) {
        g.setColor(color);
        // Create the new font to draw the symbol
        g.setFont(new Font("Monospaced", Font.PLAIN, PIECE_SIZE));
        // Convert to string so that we can use drawString()
        String glyph = ""+symbol;
        g.drawString(glyph, x - PIECE_SIZE / 2, y + PIECE_SIZE / 4);
    }
}
