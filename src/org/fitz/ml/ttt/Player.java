package org.fitz.ml.ttt;

import org.fitz.ml.constants.GameConstants;
import java.util.ArrayList;
import java.util.Scanner;

/** This class allows the computer and player to make moves in a tic-tac-toe game.
 * It takes as input a learner(knowledge) and board that allows it to
 * output a move.
 * Created by FitzRoi on 1/19/16.
 */
public class Player {
    private Learner learner;
    private Board b;

    public Player(Learner learner, Board b){
        this.learner = learner;
        this.b = b;
    }

    public Board getBoard() {
        return b;
    }

    public void setBoard(Board b) {
        this.b = b;
    }

    /**
     * This method reads an integer from the console and uses it
     * to define a user move. This integer represents a cell number in
     * the tic-tac-toe matrix, numbered from 1 - n
     * @return a user move
     */

    public Move playerMove(){
        b.printMatrix();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a move (by number): ");
        int userPos = scanner.nextInt();

        while (userPos < 0 || userPos > (GameConstants.n * GameConstants.n)) {
            System.out.println("Choose a move (by number): ");
            userPos = scanner.nextInt();
        }

        Move move = new Move(userPos);
        return move;
    }

    /**
     * This method is used by the computer to make a move
     * based in the current board (matrix).
     * It loops through all possible legal board positions and computes a score
     * based on the trained vhat method.
     *
     * @return the move that has the maximum score of all possible legal moves
     */
    public Move computerMove(){
        int bestMove = 0;
        char matrix[][] = b.getMatrix();
        int state[];
        double score;
        int posNumber = 0;
        double max = -Double.MAX_VALUE;
        ArrayList<Move> legalMoves = new ArrayList<Move>();
        for(int i = 0; i < GameConstants.n; i++){
            for(int j = 0; j < GameConstants.n; j++){
                //if move is legal, generate a board state and compute its score
                posNumber++;
             if(matrix[i][j] == 0) {
//                 System.out.println("Trying position " + pos);
                 b.placeMark(GameConstants.computerMark, i, j); //make a temporary move to check score
                 state = b.determineStateFromMatrix();
                 b.setBoardState(state);
                 score = learner.vHat(b); //compute score using updated weights in learner
                 legalMoves.add(new Move(score, i, j, posNumber));
                 b.undoPlacement(i,j);
                 b.setBoardState(b.determineStateFromMatrix());
             }

            }
        }

        //choose move with max score
        if(legalMoves.size() > 0) {
            for (int i = 0; i < legalMoves.size(); i++)
                if (legalMoves.get(i).getScore() > max) {
                    bestMove = i;
                    max = legalMoves.get(i).getScore();
                }
            return legalMoves.get(bestMove);
        }

        return null;
    }
}
