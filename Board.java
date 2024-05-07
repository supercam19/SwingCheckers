/*
    The board that the checkers game is played on. Most game logic should occur in this class,
    including game state, and player input/output.
    Extends JPanel so that we can use paintComponent() to draw onto the window.

    We use a 640x640 pixel window because checkers boards are typically perfect squares

    @author (Cameron Labelle)
    @version (April 2024)
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.awt.Color;

// Intercept mouse motion and click events with interface implementation
public class Board extends JPanel implements MouseListener, MouseMotionListener {
    // Constant information
    final int ROW_SIZE = 8;
    final int COLUMN_SIZE = 8;
    final int TILE_SIZE_PX = 60; // Window size divided by 8
    // Window size (see header)
    final int WIDTH = 640;
    final int HEIGHT = 480;
    final int PADDING = 80; // Pixels that will be on the left and right of the board.
    boolean blacksTurn = true;
    Tile[][] tiles;
    // Data for piece being moved by player at current time.
    boolean movingPiece = false;
    CheckersPiece heldPiece;
    Position2D movingPieceStart;
    Position2D movingPieceCurrent;

    // Piece movement permission integers.
    final int WHITE_PERMISSION = 0;
    final int BLACK_PERMISSION = 1;
    final int KING_PERMISSION = 2;

    // Debug only; see CheckersGame.java
    int activeChannel = 0;

    // Passed from driver to update text for whose turn it is
    JLabel gameInfo;

    // Store any tiles that CAN capture (capture rule). If not empty, one of the tiles in this list MUST be played.
    // Using a set might be more efficient, but this works fine, so I won't change it.
    ArrayList<Tile> mustCapture;

    // The amount of pieces of each color remaining.
    int whiteRemaining = 12;
    int blackRemaining = 12;
    final Color skyBlue = new Color(82, 185, 217);
    final Color treeGreen = new Color(33, 153, 0);
    final Color cloudWhite = new Color(231, 242, 228);

    // Constructor, pass JLabel for whose turn it is from driver
    public Board(JLabel gameInfo) {
        this.gameInfo = gameInfo;
        // Reset some instance data in the constructor to ensure proper resets.
        movingPieceCurrent = new Position2D();
        tiles = new Tile[8][8];
        mustCapture = new ArrayList<Tile>();
        // Tile generator
        int tileNumber = 0; // Every second tile should be playable
        // Nested for loop to go through each tile in the 2D array
        for (int v = 0; v < COLUMN_SIZE; v++) {
            for (int h = 0; h < ROW_SIZE; h++) {
                // (tileNumber % 2) != (v % 2) will create a grid of playable tiles
                tiles[h][v] = new Tile((h * TILE_SIZE_PX + PADDING), (v * TILE_SIZE_PX), (tileNumber % 2) != (v % 2));
                tileNumber++;
                // Add pieces, black first
                if (v < 3 && (tileNumber % 2) == (v % 2)) {
                    tiles[h][v].setPiece(new CheckersPiece(true));
                }
                else if (v > 4 && (tileNumber % 2) == (v % 2)) {
                    tiles[h][v].setPiece(new CheckersPiece(false));
                }
            }
        }
        // Initialize all piece moves
        determineMoves();
        // Board size (640, 640)
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // All graphics for the program in this method
    // Call with repaint() to update
    public void paintComponent(Graphics brush) {
        super.paintComponent(brush);
        // Draw the grid of tiles by calling each tile's draw() method
        drawBackdrop(brush);
        for (Tile[] row : tiles) {
            for (Tile t : row) {
                t.draw(brush);
            }
        }
        // Draw piece held in the players "hand" (being moved by mouse)
        if (movingPiece) {
            heldPiece.drawCenteredAt(movingPieceCurrent.x, movingPieceCurrent.y, brush);
        }

        for (int i = 12; i > whiteRemaining; i--) {
            CheckersPiece pc = new CheckersPiece(false);
            pc.drawCenteredAt(40, i * 30, brush);
        }
        for (int i = 12; i > blackRemaining; i--) {
            CheckersPiece pc = new CheckersPiece(true);
            pc.drawCenteredAt(WIDTH - 40, i * 30, brush);
        }
    }

    public void drawBackdrop(Graphics brush) {
        brush.setColor(skyBlue);
        brush.fillRect(0, 0, WIDTH, HEIGHT);

        brush.setColor(treeGreen);
        int[] xPoints = {550, 590, 630};
        int[] yPoints = {500, 420, 500};
        brush.fillPolygon(xPoints, yPoints, 3);

        int[] xPoints2 = {-50, 10, 60};
        int[] yPoints2 = {HEIGHT, 390, HEIGHT};
        brush.fillPolygon(xPoints2, yPoints2, 3);

        brush.setColor(cloudWhite);
        brush.fillOval(10, 50, 50, 50);
        brush.fillOval(25, 40, 50, 50);
        brush.fillOval(55, 60, 50, 50);

        brush.fillOval(560, 120, 50, 50);
        brush.fillOval(585, 105, 50, 50);
        brush.fillOval(575, 145, 50, 50);
    }

    /*
        Intercept mouse press events thanks to MouseListener implementation
        We use this instead of mouseClicked() to make the controls "drag and drop"
     */
    @Override
    public void mousePressed(MouseEvent e) {
        log(2, "Black's turn: " + blacksTurn);
        // Convert pixel position to tile space
        int tileX = (e.getX() - PADDING) / TILE_SIZE_PX;
        int tileY = e.getY() / TILE_SIZE_PX;
        Tile targeted = tiles[tileX][tileY];
        // Check if tile holds piece of same color as whose turn it is
        if (targeted.holdsPiece && targeted.piece.black == blacksTurn) {
            // Verify this does not violate capture rule
            if (mustCapture.isEmpty() || mustCapture.contains(targeted)) {
                // Move important piece info into instance memory
                movingPiece = true;
                heldPiece = tiles[tileX][tileY].takePiece();
                movingPieceStart = new Position2D(tileX, tileY);
                movingPieceCurrent.set(e.getX(), e.getY());
                // Highlight all possible movements for this piece (including back to original tile)
                tiles[tileX][tileY].setHighlight(true);
                for (Tile t : heldPiece.moves) {
                    t.setHighlight(true);
                }
                // Apply the highlights
                repaint();
            }
        }
        // Give a popup dialog if the player tries to move a piece of the opposite colour.
        else if (targeted.holdsPiece) {
            if (blacksTurn)
                showDialog("Illegal move!", "It is black's turn, they need to move a black piece.", JOptionPane.ERROR_MESSAGE);
            else
                showDialog("Illegal move!", "It is white's turn, they need to move a white piece.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
        Important method for handling user control
        Intercept mouse release events only if the user is moving a piece (from mousePressed())
        From here, determine if the move was legal, and take actions necessary
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (movingPiece) {
            // Convert pixel position to tile space
            int tileX = (e.getX() - PADDING) / TILE_SIZE_PX;
            int tileY = e.getY() / TILE_SIZE_PX;
            // Return early if mouse was released outside the window. (Assuming standard res)
            if (!(onBoard(tileX) && onBoard(tileY))) {
                tiles[movingPieceStart.x][movingPieceStart.y].setPiece(heldPiece);
                movingPiece = false;
                deHighlight();
                repaint();
                return;
            }
            Tile endTile = tiles[tileX][tileY];
            // success: piece moved from mouse release (includes jumps)
            // wasJump: piece jumped from mouse release
            boolean success = false;
            boolean wasJump = false;
            // When a piece is picked up, all possible moves are highlighted, so undo it now.
            tiles[movingPieceStart.x][movingPieceStart.y].setHighlight(false);
            for (Tile t : heldPiece.moves) {
                if (endTile.equals(t)) {
                    wasJump = movePiece(tileX, tileY);
                    success = true;
                }
                t.setHighlight(false);
            }
            // Movement was not legal
            if (!success) {
                // Put the "held" piece back to where it started.
                tiles[movingPieceStart.x][movingPieceStart.y].setPiece(heldPiece);
                movingPiece = false;
                // Have to put a repaint here because the popup will stop this thread of execution.
                // With no repaint, it will look like the piece is staying in the illegal position.
                repaint();
                // Also, don't show the message if the player didn't move the piece at all
                if (!endTile.equals(tiles[movingPieceStart.x][movingPieceStart.y]))
                    showDialog("Illegal Move!", "That was not a legal movement for your piece!", JOptionPane.ERROR_MESSAGE);
            }
            // Movement was legal and it captured an enemy piece
            else if (success && wasJump) {
                // Determine if the piece that just captured can capture again
                mustCapture.clear();
                findMoves(endTile);
                // Can capture
                if (!mustCapture.isEmpty()) {
                    // Indicate to user next move, don't switch turn yet
                    dimNonCaptures(true);
                }
                // Cannot capture
                else {
                    switchTurn();
                }
                tiles[movingPieceStart.x][movingPieceStart.y].setHighlight(false);
            }
            // Successful movement, but not a capture : just switch turns
            else if (success) {
                switchTurn();
            }
            // Remove the piece the player was moving from instance memory, it now belongs to the tile it originated from
            movingPiece = false;
            heldPiece = null;
            movingPieceStart = null;
            repaint();
        }
    }

    // Helper method to be called whenever a player ends their turn
    public void switchTurn() {
        log(2, "Switch turn called");
        // IMPORTANT to switch turns before calling determineMoves()
        blacksTurn = !blacksTurn;
        determineMoves();

        deHighlight();
        // Create dimming effect to show only the pieces that can capture
        dimNonCaptures(!mustCapture.isEmpty());
        if (blacksTurn)
            gameInfo.setText("Black's turn");
        else
            gameInfo.setText("White's turn");
    }

    /*
    Calculate all possible piece movements
    Iterate through every tile and run the findMoves() method on it.
    Only need to check tiles that hold a piece of the same color as whose turn it is
     */
    public void determineMoves() {
        mustCapture.clear();
        for (Tile[] ts : tiles) {
            for (Tile t : ts) {
                if (t.playable && t.holdsPiece && t.piece.black == blacksTurn) {
                    findMoves(t);
                }
            }
        }
    }

    /*
    Find every possible move for a given piece, if any captures are possible, only consider those
    Store moves to memory in order to be compared against when trying to move pieces.
     */
    public void findMoves(Tile tl) {
        tl.piece.moves.clear();
        boolean canCapture = false;
        // First, check if captures are possible
        log(1, "Directions: " + Direction2D.directionsFromPermission(movePermission(tl.piece)).length);
        for (Vector2DInt dir : Direction2D.directionsFromPermission(movePermission(tl.piece))) {
            // Convert tile pixel position to grid space
            int tx = (tl.x - PADDING) / TILE_SIZE_PX;
            int ty = tl.y / TILE_SIZE_PX;
            // Check if targeted tile is on the board, avoid IndexOutOfBounds
            log(1, "dir.x: " + dir.x);
            log(1, "dir.y: " + dir.y);
            log(1, "Checking new: " + (tx + dir.x) + ", " + (ty + dir.y));
            if (onBoard(tx + dir.x) && onBoard(ty + dir.y)) {
                log(1, "Position on board");
                Tile targeted = tiles[tx + dir.x][ty + dir.y];
                // Check if neighbor tile holds enemy piece
                if (targeted.holdsPiece && targeted.piece.black != tl.piece.black) {
                    log(1, "Neighbor is enemy");
                    dir.step();
                    log(1, "Checking: " + (tx + dir.x) + ", " + (ty + dir.y));
                    // Check if the tile behind the neighbor exists (in range of the 8x8 board)
                    if (onBoard(tx + dir.x) && onBoard(ty + dir.y)) {
                        log(1, "Position on board");
                        targeted = tiles[tx + dir.x][ty + dir.y];
                        // If the tile behind the neighbor is empty, a jump can be made
                        if (!targeted.holdsPiece) {
                            log(1, "Tile is empty, possible jump");
                            tl.piece.moves.add(targeted);
                            canCapture = true;
                            mustCapture.add(tl);
                        }
                    }
                }
            }
        }
        // If no captures are possible, we can check for normal movements
        if (!canCapture) {
            for (Vector2DInt dir : Direction2D.directionsFromPermission(movePermission(tl.piece))) {
                // Pixels to grid space
                int tx = (tl.x - PADDING) / TILE_SIZE_PX;
                int ty = tl.y / TILE_SIZE_PX;
                // Make sure the targeted tile exists (in range of 8x8 board)
                if (onBoard(tx + dir.x) && onBoard(ty + dir.y)) {
                    Tile targeted = tiles[tx + dir.x][ty + dir.y];
                    if (!targeted.holdsPiece) {
                        tl.piece.moves.add(targeted);
                    }
                }
            }
        }
    }

    // Check if a 1D coordinate is on the board (from 0 to 7)
    public boolean onBoard(int val) {
        return val > -1 && val < 8;
    }

    // Use CheckersPiece data to determine it movement permission
    // Kings move different from whites, different from blacks
    public int movePermission(CheckersPiece pc) {
        if (pc.crowned)
            return KING_PERMISSION;
        else if (pc.black)
            return BLACK_PERMISSION;
        else
            return WHITE_PERMISSION;
    }

    /*
        Call this method when moving a piece to a new tile, DON'T CALL Tile.setPiece() DIRECTLY
        This method will move the piece, check if it should become king from that movement,
        |->check if the movement was a capture
           |->if yes, remove the captured piece from the board, check if the game is over
              |-> if yes, show end of game dialog
        |->redraw graphics
     */
    public boolean movePiece(int tileX, int tileY) {
        tiles[tileX][tileY].setPiece(heldPiece);
        // Logically, any piece moved to the outer rows would become king
        if (tileY == 0 || tileY == 7)
            tiles[tileX][tileY].piece.crown();

        // Check if move was jump, if yes, remove captured piece.
        Vector2DInt movement = Vector2DInt.generateVector(movingPieceStart, new Position2D(tileX, tileY));
        Vector2DInt normalized = Vector2DInt.copy(movement);
        normalized.normalize();
        if (!movement.equals(normalized)) {
            // Movement was a capture, remove piece from board
            CheckersPiece taken = tiles[movingPieceStart.x + normalized.x][movingPieceStart.y + normalized.y].takePiece();
            repaint();
            // Check color of removed piece, and if that was the last piece of that color.
            if (taken.black) {
                blackRemaining--;
                if (blackRemaining == 0) {
                    showDialog("Game over!", "White wins!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else {
                whiteRemaining--;
                if (whiteRemaining == 0) {
                    showDialog("Game over!", "Black wins!", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            return true;
        }
        repaint();
        return false;
    }

    // Remove highlight from all tiles on the board
    public void deHighlight() {
        for (Tile[] ts : tiles) {
            for (Tile t : ts) {
                if (t.playable)
                    t.setHighlight(false);
            }
        }
    }

    // Helper method to make showing dialog boxes easier
    public void showDialog(String title, String msg, int type) {
        JOptionPane.showMessageDialog(
                null,
                msg,
                title,
                type
        );
    }

    // Method for debugging. Uncomment debugButton in CheckersGame.java and set channel in console.
    // Only log calls that match the current debug channel will appear in the console.
    private void log(int channel, String str) {
        if (channel == activeChannel)
            System.out.println(str);
    }

    // Apply visual dim effect to tiles that are not 'involved in capture' (see involvedInCapture())
    public void dimNonCaptures(boolean flag) {
        for (Tile[] ts : tiles) {
            for (Tile t : ts) {
                if (!involvedInCapture(t))
                    t.dimmed = flag;
                else
                    t.dimmed = !flag;
            }
        }
        repaint();
    }

    // Return true if this tile holds a piece that can capture another piece
    // or if this tile can be the end location of a capture.
    public boolean involvedInCapture(Tile t) {
        if (mustCapture.contains(t))
            return true;
        for (Tile tl : mustCapture) {
            if (tl.piece.moves.contains(t))
                return true;
        }
        return false;
    }

    // Source for the piece dragging visual effect, intercept mouse dragging with MouseMotionListener implementation
    @Override
    public void mouseDragged(MouseEvent e) {
        movingPieceCurrent.set(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
