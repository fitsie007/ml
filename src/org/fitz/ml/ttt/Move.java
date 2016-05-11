package org.fitz.ml.ttt;

/** This class facilitates moves by the computer and user.
 * Computer moves feature a score which is used to select the best
 * moves of all legal moves on a board.
 *
 * Created by FitzRoi on 1/19/16.
 */
public class Move {
    private double score;
    private int row;
    private int col;
    private int posNumber;

    /**
     * constructor used by the computer to initialize a move
     * @param score the score used by the computer to determine best move
     * @param row the row of the tic-tac-toe matrix
     * @param col the column of the tic-tac-toe matrix
     * @param posNumber the cell-number of the tic-tac-toe matrix (numbered from 1 - n)
     */
    public Move(double score, int row, int col, int posNumber){
        this.score = score;
        this.row = row;
        this.col = col;
        this.posNumber = posNumber;
    }

    /**
     * constructor used by the computer to initialize a move
     * @param posNumber the cell-number of the tic-tac-toe matrix (numbered from 1 - n)
     */

    public Move(int posNumber){
        this.score = -Double.MAX_VALUE;
        this.row = -1;
        this.col = -1;
        this.posNumber = posNumber;
    }

    public double getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getPosNumber() {
        return posNumber;
    }

    public void setPosNumber(int posNumber) {
        this.posNumber = posNumber;
    }
}
