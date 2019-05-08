/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author Clopst
 */
public class Sudoku extends JFrame {

    private static final int GRID_SIZE = 9;

    private final int NO_VALUE = 0;

    private static final int CELL_SIZE = 60;
    private static final int CANVAS_WIDTH = CELL_SIZE * GRID_SIZE;
    private static final int CANVAS_HEIGHT = CELL_SIZE * GRID_SIZE;

    private static final Color DARK_BGCOLOR = new Color(210, 210, 210);
    private static final Color LIGHT_BGCOLOR = new Color(240, 240, 240);
    private static final Font FONT_NUMBERS = new Font("Monospaced", Font.BOLD, 20);

    // The cells
    private JTextField[][] tfCells = new JTextField[GRID_SIZE][GRID_SIZE];

    // 9x9 2D array for the board
    private int[][] board = {
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    private int[][] boardSolved = new int[GRID_SIZE][GRID_SIZE];

    Container cp = getContentPane();
    InputListener inputListener = new InputListener();

    private File boardFile;

    public Sudoku() {
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);
        
        JMenuItem newGameItem = new JMenuItem("New game");
        gameMenu.add(newGameItem);
        JFileChooser chooser = new JFileChooser(new File(File.separator + "tmp"));
        newGameItem.addActionListener(new OpenFileAction(this, chooser));
        
        JMenuItem solveItem = new JMenuItem("Solve this board");
        gameMenu.add(solveItem);
        solveItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int row = 0; row < GRID_SIZE; ++row) {
                    for (int col = 0; col < GRID_SIZE; ++col) {
                        if (!tfCells[row][col].getText().equals(String.valueOf(boardSolved[row][col]))) {
                            tfCells[row][col].setText(String.valueOf(boardSolved[row][col]));
                            tfCells[row][col].setForeground(Color.BLUE);
                        }
                    }
                }
            }
        });
        
        gameMenu.addSeparator();
        JMenuItem aboutItem = new JMenuItem("About");
        gameMenu.add(aboutItem);
        aboutItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Dimas Nurfauzi \"clopst\"; 2019", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
      
        setJMenuBar(menuBar);

        cp.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE)); // 9x9

        constructCells();
        solve();
        colorizeSubgrid();
    }

    public static void main(String[] args) {
        Sudoku sudokuJFrame = new Sudoku();
    }

    /**
     * Create the solved board
     */
    private void solve() {
        // Sudoku solver
        SudokuSolver sudokuSolver = new SudokuSolver(board);
        boardSolved = sudokuSolver.boardSolved();
    }

    /**
     * Construct the cells to the grid layout
     */
    public void constructCells() {
        // Construct 9x9 JTextFields and add to the content-pane
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (tfCells[row][col] != null) {
                    cp.remove(tfCells[row][col]);
                }
                tfCells[row][col] = new JTextField(); // Allocate element of array
                cp.add(tfCells[row][col]);            // ContentPane adds JTextField

                if (board[row][col] == NO_VALUE) {
                    tfCells[row][col].setText("");     // set to empty string
                    tfCells[row][col].setEditable(true);
                    tfCells[row][col].addKeyListener(inputListener);
                } else {
                    tfCells[row][col].setText(board[row][col] + "");
                    tfCells[row][col].setEditable(false);
                }

                // Beautify all the cells
                tfCells[row][col].setHorizontalAlignment(JTextField.CENTER);
                tfCells[row][col].setFont(FONT_NUMBERS);
            }
        }

        cp.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Handle window closing
        setTitle("Sudoku");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Read from text file
     */
    public void readFile() {
        String input = "";
        String[] inputArr;

        try (FileReader fileReader = new FileReader(boardFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            input = bufferedReader.readLine();
        } catch (Exception e) {
            System.err.format("IOException: %s%n", e);
        }

        inputArr = input.split("");

        int row = 0;
        int col = 0;
        for (int cell = 0; cell < inputArr.length; cell++) {
            board[row][col] = Integer.parseInt(inputArr[cell]);
            if (((cell + 1) % 9) == 0) {
                row++;
                col = 0;
            } else {
                col++;
            }
        }
    }

    public class OpenFileAction extends AbstractAction {

        JFrame frame;
        JFileChooser chooser;

        OpenFileAction(JFrame frame, JFileChooser chooser) {
            super("Open board text file");
            this.frame = frame;
            this.chooser = chooser;
        }

        public void actionPerformed(ActionEvent evt) {
            chooser.showOpenDialog(frame);

            boardFile = chooser.getSelectedFile();
            readFile();
            constructCells();
            colorizeSubgrid();
            solve();
        }
    }

    /**
     * Add colors to 9 sub-grid
     */
    private void colorizeSubgrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                // First three rows
                if (row >= 0 && row <= 2) {
                    // First three columns
                    if (col >= 0 && col <= 2) {
                        tfCells[row][col].setBackground(DARK_BGCOLOR);
                    }
                    // Middle three columns
                    if (col >= 3 && col <= 5) {
                        tfCells[row][col].setBackground(LIGHT_BGCOLOR);
                    }
                    // Lsat three columns
                    if (col >= 6 && col <= 8) {
                        tfCells[row][col].setBackground(DARK_BGCOLOR);
                    }
                }

                // Middle three rows
                if (row >= 3 && row <= 5) {
                    // First three columns
                    if (col >= 0 && col <= 2) {
                        tfCells[row][col].setBackground(LIGHT_BGCOLOR);
                    }
                    // Middle three columns
                    if (col >= 3 && col <= 5) {
                        tfCells[row][col].setBackground(DARK_BGCOLOR);
                    }
                    // Last three columns
                    if (col >= 6 && col <= 8) {
                        tfCells[row][col].setBackground(LIGHT_BGCOLOR);
                    }
                }

                // Last three rows
                if (row >= 6 && row <= 8) {
                    // First three columns
                    if (col >= 0 && col <= 2) {
                        tfCells[row][col].setBackground(DARK_BGCOLOR);
                    }
                    // Middle three columns
                    if (col >= 3 && col <= 5) {
                        tfCells[row][col].setBackground(LIGHT_BGCOLOR);
                    }
                    // Last three columns
                    if (col >= 6 && col <= 8) {
                        tfCells[row][col].setBackground(DARK_BGCOLOR);
                    }
                }
                // All foreground black
                tfCells[row][col].setForeground(Color.BLACK);
                tfCells[row][col].setBorder(new LineBorder(Color.BLACK, 1));
            }
        }
    }

    private class InputListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (!((e.getKeyChar() >= '1') && (e.getKeyChar() <= '9')
                    || (e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
                    || (e.getKeyChar() == KeyEvent.VK_DELETE))) {
                e.consume();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            // All the 9*9 JTextFileds invoke this handler. We need to determine
            // which JTextField (which row and column) is the source for this invocation.
            int rowSelected = 0;
            int colSelected = 0;

            // Get the source object that fired the event
            JTextField source = (JTextField) e.getSource();
            // Scan JTextFileds for all rows and columns, and match with the source object
            for (int row = 0; row < GRID_SIZE; ++row) {
                for (int col = 0; col < GRID_SIZE; ++col) {
                    if (tfCells[row][col] == source) {
                        rowSelected = row;
                        colSelected = col;
                        break;
                    }
                }
            }

            // Check cells
            if (!tfCells[rowSelected][colSelected].getText().equals("")) {
                int input = Integer.parseInt(tfCells[rowSelected][colSelected].getText());
                if (input == boardSolved[rowSelected][colSelected]) {
                    tfCells[rowSelected][colSelected].setForeground(Color.BLUE);
                } else {
                    tfCells[rowSelected][colSelected].setForeground(Color.BLACK);
                }
            }
        }

    }

}
