/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.stream.IntStream;

/**
 *
 * @author Clopst
 */
public class SudokuSolver {

    /**
     * @param args the command line arguments
     */
    private final int GRID_START_INDEX = 0;
    private final int GRID_SIZE = 9;
    private final int SUBGRID_SIZE = 3;
    private final int NO_VALUE = 0;
    private final int MIN_VALUE = 1;
    private final int MAX_VALUE = 9;
    
    private int[][] board = new int[GRID_SIZE][GRID_SIZE];
    
    public SudokuSolver(int[][] board) {
        this.board = board;
    }
    
    public int[][] boardSolved() {
        if (solve(board)) {
            return board;
        }
        return null;
    }
    
    private boolean solve(int[][] board) {
        for (int row = GRID_START_INDEX; row < GRID_SIZE; row++) {
            for (int column = GRID_START_INDEX; column < GRID_SIZE; column++) {
                if (board[row][column] == NO_VALUE) {
                    for (int i = MIN_VALUE; i <= MAX_VALUE; i++) {
                        board[row][column] = i;
                        if (isValid(board, row, column) && solve(board)) {
                            return true;
                        }
                        board[row][column] = NO_VALUE;
                    }
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private boolean isValid(int[][] board, int row, int column) {
        return (rowConstraint(board, row) 
                && columnConstraint(board, column)
                && subsectionConstraint(board, row, column));
    }
    
    private boolean rowConstraint(int[][] board, int row) {
        boolean[] constraint = new boolean[GRID_SIZE];
        
        return IntStream.range(GRID_START_INDEX, GRID_SIZE)
                .allMatch(column -> checkConstraint(board, row, constraint, column));
    }
    
    private boolean columnConstraint(int[][] board, int column) {
        boolean[] constraint = new boolean[GRID_SIZE];
        
        return IntStream.range(GRID_START_INDEX, GRID_SIZE)
                .allMatch(row -> checkConstraint(board, row, constraint, column));
    }
    
    private boolean subsectionConstraint(int[][] board, int row, int column) {
        boolean[] constraint = new boolean[GRID_SIZE];
        
        int subgridRowStart = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subgridRowEnd = subgridRowStart + SUBGRID_SIZE;
        
        int subgridColumnStart = (column / SUBGRID_SIZE) * SUBGRID_SIZE;
        int subgridColumnEnd = subgridColumnStart + SUBGRID_SIZE;
        
        for (int i = subgridRowStart; i < subgridRowEnd; i++) {
            for (int j = subgridColumnStart; j < subgridColumnEnd; j++) {
                if (!checkConstraint(board, i, constraint, j)) return false;
            }
        }
        
        return true;
    }
    
    boolean checkConstraint(int[][] board, int row, boolean[] constraint, int column) {
        if (board[row][column] != NO_VALUE) {
            if (!constraint[board[row][column] - 1]) {
                constraint[board[row][column] - 1] = true;
            } else {
                return false;
            }
        }
        return true;
    }
    
    private void printBoard() {
        for (int row = GRID_START_INDEX; row < GRID_SIZE; row++) {
            for (int column = GRID_START_INDEX; column < GRID_SIZE; column++) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println();
        }
    }
}
