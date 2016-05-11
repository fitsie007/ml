package org.fitz.ml.ttt;

import org.fitz.ml.constants.GameConstants;

import java.util.Random;

/** This class provides functionalities and properties for a game board
 * such as updating the board state (X values), the tic-tac-toe matrix,
 * and determining the Xs from the matrix
 *
 * Created by FitzRoi on 1/19/16.
 */
public class Board {
    private int state[];
    private int index;
    private char matrix[][];

    public Board(int index, int state[]){
        this.state = state;
        this.index = index;
        this.matrix = new char[GameConstants.n][GameConstants.n];
    }
    public Board(){
        this.state = new int[GameConstants.numParams];
        this.index = -1;
        this.matrix = new char[GameConstants.n][GameConstants.n];
    }

    public int[] getBoardState() {
        return state;
    }

    public void setBoardState(int[] state) {
        this.state = state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public char[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(char[][] matrix) {
        this.matrix = matrix;
    }

    public void printBoardState(){
        for(int i=0;i<state.length;i++)
            System.out.println("x" + i + ": \t" + state[i]);
    }

    /**
     * This method pretty-prints a game matrix, numbering the unfilled slots
     * and placing the user and computer marks in the filled slots.
     * It helps the player identify a cell to place a mark by using a number.
     */
    public void printMatrix(){
        int count = 0;
        System.out.println(new String(new char[GameConstants.n * 2 + 3]).replace("\0", "--"));
        for(int i = 0; i < GameConstants.n; i++){
            System.out.print("|");
            for(int j = 0; j < GameConstants.n; j++){
                count++;
                if(matrix[i][j] != GameConstants.computerMark && matrix[i][j] != GameConstants.playerMark )
                    System.out.print(" " +count + "  | ");
                else
                    System.out.print(" " + matrix[i][j] + "  | ");
            }
            System.out.println("\n" + new String(new char[GameConstants.n * 2 + 3]).replace("\0", "--"));
        }

    }

    /**
     * This method determines the board state (X values) from a character matrix
     * of player and computer marks
     * @return the X1..Xn linear array
     */
    public int[] determineStateFromMatrix(){
        int rowWins[] = new int[GameConstants.n];
        int colWins[] = new int[GameConstants.n];
        int diagWins[] = new int[2];
        int rowLoss[] = new int[GameConstants.n];
        int colLoss[] = new int[GameConstants.n];
        int diagLoss[] = new int[2];

        for (int i = 0; i < GameConstants.n; i++) {
            rowWins[i] = rowCount(GameConstants.computerMark, i);
            colWins[i] = colCount(GameConstants.computerMark, i);
            rowLoss[i] = rowCount(GameConstants.playerMark, i);
            colLoss[i] = colCount(GameConstants.playerMark, i);
        }
        diagWins[0] = diagCount( GameConstants.computerMark, 1);
        diagWins[1] = diagCount(GameConstants.computerMark, 0);
        diagLoss[0] = diagCount(GameConstants.playerMark, 1);
        diagLoss[1] = diagCount(GameConstants.playerMark, 0);

        int boardState[] = mergeArrays(new int[]{1}, rowWins, colWins, diagWins, rowLoss, colLoss, diagLoss);
        return boardState;
    }

    public void makeRandomMove(char mark){
        int pos = 1 + new Random().nextInt(GameConstants.n * GameConstants.n + 1);
        while(!placeMark(mark,pos)){
            pos = 1 + new Random().nextInt(GameConstants.n * GameConstants.n + 1);
            setBoardState(determineStateFromMatrix());
            if(win() || lose() || tie())
                break;
        }
    }


    /**
     * This method places a mark (character) in the tic-tac-toe matrix.
     * It's used during game play
     * @param mark the mark (character) to put in a cell
     * @param pos a number from 1 - n identifying a cell in the matrix
     * @return true if mark was placed successfully, false if position already filled
     */
    public boolean placeMark(char mark, int pos){
        int coord[]={0,0};
        int count = 0;

        for(int i = 0; i < GameConstants.n; i++){
            for(int j = 0; j < GameConstants.n; j++){
                count++;
                if(count == pos) {
                    coord = new int[]{i, j};
                    break;
                }
            }
        }

        if(matrix[coord[0]][coord[1]] != 0)
            return false;
        else{
            matrix[coord[0]][coord[1]] = mark;
        }

        return true;
    }

    /**
     * This method places a mark(character) in the tic-tac-toe matrix.
     * It's used during game play
     * @param mark the mark (character) to put in a cell
     * @param i the ith row of a matrix
     * @param j the jth column of a matrix
     * @return true if mark was placed successfully, false if position already filled
     */
    public boolean placeMark(char mark, int i, int j){

        if(i >= 0 && j >= 0 && i < GameConstants.n && j < GameConstants.n) {
            if (matrix[i][j] != 0)
                return false;
            else {
                matrix[i][j] = mark;
                return true;
            }
        }
        return false;
    }

    /**
     * This method undoes the placement of a mark at a certain position in the game matrix.
     * Placement of a mark is sometimes done temporarily to help determine
     * the best move for the computer to win the game.
     * @param pos the cell number of the matrix (1...n)
     */
    public void undoPlacement(int pos){
        int count = 0;
        for(int i = 0; i < GameConstants.n; i++){
            for(int j = 0; j < GameConstants.n; j++){
                count++;
                if(count == pos) {
                    matrix[i][j] = 0;
                    break;
                }
            }
        }
    }

    /**
     * This method undoes the placement of a mark at a certain position in the game matrix.
     * Placement of a mark is sometimes done temporarily to help determine
     * the best move for the computer to win the game.
     * @param i the ith row of a matrix
     * @param j the jth column of a matrix
     */
    public void undoPlacement(int i, int j){
        matrix[i][j] = 0;
    }

    /**
     * This method merges n int arrays into one array.
     * It is particularly used to create the X linear array X1 ... Xn
     * @param arrays int 1D arrays to merge
     * @return merged int array
     */
    public int[] mergeArrays(int []...arrays){
        int mergedArray[]  = new int[GameConstants.numParams];
        int i=0;
        for (int[] array: arrays)
        {
            System.arraycopy(array, 0, mergedArray, i, array.length);
            i += array.length;
        }
        return mergedArray;
    }

    /**
     * This method determines if the computer wins the game by counting computer marks
     * in the game matrix.
     * @return true if computer wins, false otherwise
     */

    public boolean win(){
        for (int i = 0; i < GameConstants.n; i++) {
            if(rowCount(GameConstants.computerMark, i) == GameConstants.n)
                return true;
            if(colCount(GameConstants.computerMark, i) == GameConstants.n)
                return true;
        }

        //count diagonals
        if(diagCount(GameConstants.computerMark, 0) == GameConstants.n)
            return true;
        if(diagCount(GameConstants.computerMark, 1) == GameConstants.n)
            return true;

        return false;
    }


    /**
     * This method determines if the computer loses the game by counting player marks
     * in the game matrix.
     * @return true if computer loses, false otherwise
     */
    public boolean lose(){

        for (int i = 0; i < GameConstants.n; i++) {
          if(rowCount(GameConstants.playerMark, i) == GameConstants.n)
                return true;
            if(colCount(GameConstants.playerMark, i) == GameConstants.n)
                return true;
        }

        if(diagCount(GameConstants.playerMark, 0) == GameConstants.n)
            return true;
        if(diagCount(GameConstants.playerMark, 1) == GameConstants.n)
            return true;

        return false;
    }


    /**
     * This method uses the fullboard technique to call a tie.
     * Must be called after win() and lose()
     * @return true if board is full, false otherwise
     */
    public boolean tie(){
        int count = 0;
        for (int i = 0; i < GameConstants.n; i++) {
           for(int j = 0; j < GameConstants.n; j++)
               if(matrix[i][j] == GameConstants.computerMark || matrix[i][j] == GameConstants.playerMark)
                   count++;
        }

        return count == GameConstants.n * GameConstants.n;
    }

    /**
     * This method counts the number of a certain character in a certain row of the tic-tac-toe matrix.
     * This value is used to help create the X linear array (X1 ... Xn)
     * @param playerMark the mark to count
     * @param row the row to check for the given mark
     * @return number of times mark appears in a row
     */
    public int rowCount(char playerMark, int row){
        int rowCount=0;
        for(int i=0; i< GameConstants.n; i++)
            if(matrix[row][i] == playerMark)
                rowCount++;
        return rowCount;

    }

    /**
     * This method counts the number of a certain character in a certain column of the tic-tac-toe matrix.
     * This value is used to help create the X linear array (X1 ... Xn)
     * @param playerMark the mark to count
     * @param col the column to check for the given mark
     * @return number of times mark appears in a column.
     */
    public int colCount(char playerMark, int col){
        int colCount=0;
        for(int i=0; i< GameConstants.n; i++)
            if(matrix[i][col] == playerMark)
                colCount++;
        return colCount;

    }

    /**
     * This method counts the number of a certain character in a certain diagonal of the tic-tac-toe matrix.
     * This value is used to help create the X linear array (X1 ... Xn)
     * @param playerMark the mark to count
     * @param diag the diagonal (1 or 2) to check for the given mark
     * @return number of times mark appears in a diagonal.
     */
    public int diagCount(char playerMark, int diag){
        int diagCount=0;
        for(int i= GameConstants.n-1, j=0; i>=0; i--, j++) {
            if (diag == 1) {
                if (matrix[i][i] == playerMark)
                    diagCount++;
            }
            else
            if (matrix[i][j] == playerMark)
                diagCount++;
        }

        return diagCount;
    }

}
