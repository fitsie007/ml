package org.fitz.ml.main;

import org.fitz.ml.constants.GameConstants;
import org.fitz.ml.ttt.TicTacToe;

/** This is the main class for playing the tic-tac-toe game.
 * parameters: -experience: ("teacher" or "noteacher")
 *             -iterations: number of times to train on the same number of games (20 games by default)
 *             -file: path to the training file for playing in teacher mode
 * Created by FitzRoi on 1/22/16.
 */
public class PlayTicTacToeGame {
    public static void main(String[] args){
        int mode = GameConstants.teacher;
        int iterations = 1;
        String trainingFile = GameConstants.TEACHER_TRAINING_FILE;

        if (args.length > 0) {
            for(int i=0;i<args.length;i++) {
                if(args[i].equals("-experience")) {
                    if(i+1 < args.length)
                        mode = args[i+1].equals("teacher")? GameConstants.teacher: GameConstants.noTeacher;
                    i++;
                }

                else if(args[i].equals("-iterations")) {
                    if(i+1 < args.length)
                        iterations = Integer.parseInt(args[i+1]);
                    i++;
                }
                else if(args[i].equals("-file")) {
                    if(i+1 < args.length)
                        trainingFile = args[i+1];
                    i++;
                }
            }
        }

        TicTacToe ttt = new TicTacToe(iterations);
        if(mode == GameConstants.teacher)
            ttt.testTeacher(trainingFile);
        else
            ttt.testNoTeacher();
    }
}
