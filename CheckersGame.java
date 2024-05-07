/*
    The driver class for the checkers game. Manages the window, which includes a Board and
    reset JButton. Implements ActionListener in order to give functionality to reset button

    @author (Cameron Labelle)
    @version (April 2024)
 */

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import java.util.Scanner;

public class CheckersGame implements ActionListener {

    // Window widgets
    JFrame frame;
    JPanel buttonPanel;
    JButton resetButton;
    JButton debugButton;
    JLabel gameInfo;
    Board gameBoard;

    Scanner in;

    public CheckersGame() {
        // Initialize window
        frame = new JFrame();
        frame.setTitle("Game of Checkers - Cameron");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        in = new Scanner(System.in);

        // Initialize widgets
        buttonPanel = new JPanel();
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        debugButton = new JButton("Set channel");
        debugButton.addActionListener(this);
        gameInfo = new JLabel("Black's turn");
        gameBoard = new Board(gameInfo);

        // Add widgets to JPanels, then JPanels to JFrame
        buttonPanel.add(resetButton);
        // buttonPanel.add(debugButton); // Not needed in prod
        buttonPanel.add(gameInfo);

        frame.getContentPane().add(gameBoard, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    // Start the program from cmd line
    public static void main(String[] args) {
        new CheckersGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button actions
        if (e.getSource() == resetButton) {
            // Reset game by initializing a new Board instance, add that to frame.
            gameInfo.setText("Black's turn");
            frame.getContentPane().remove(gameBoard);
            gameBoard = new Board(gameInfo);
            frame.getContentPane().add(gameBoard);
            // Update window
            frame.revalidate();
        }
        else if (e.getSource() == debugButton) {
            // Debug; set the channel in the console as an int
            // 0 - Silent output
            // 1 - Movement generator debug
            // 2 - Turn debug

            System.out.print("Input new channel: ");
            gameBoard.activeChannel = in.nextInt();
            in.nextLine();
        }
    }
}