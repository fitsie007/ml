package org.fitz.ml.ttt;

import org.fitz.ml.constants.GameConstants;
import java.util.ArrayList;
import java.util.Random;

/** This class helps to run the learning algorithm, which will provide the knowledge
 * for the tic-tac-toe game
 * Experience ---> [ learner ] ---> knowledge
 * Created by FitzRoi on 1/19/16.
 */
public class Learner {

    private Experience experience;
    private double w[];
    private int gameSteps;


    public Learner(Experience E){
        this.gameSteps = 0;
        this.experience = E;
        this.w = new double[GameConstants.numParams];
    }

    /**
     * This method initializes random weights to start off the training process
     */
    public void initWeights(){
        Random rand = new Random();
        for (int i = 0; i < GameConstants.numParams; i++)
            w[i] = GameConstants.min + (GameConstants.max - GameConstants.min) * rand.nextDouble();
    }

    public double[] getW() {
        return this.w;
    }

    public void setW(double[] w) {
        this.w = w;
    }

    public int getGameSteps() {
        return gameSteps;
    }

    public void setGameSteps(int gameSteps) {
        this.gameSteps = gameSteps;
    }

    /**
     * This method uses max sum of attributes to determine who started the game
     * @param b a board (the first board in a game)
     * @return an integer representing who started the game (computer or player)
     */
    public int getStarter(Board b){
        int computerSum = 0;
        int playerSum = 0;
        int x[] = b.getBoardState();
        int playerPos =  GameConstants.numParams/2;
        for(int i = 1; i <= playerPos; i++) { //skip x0
            computerSum += x[i];
            playerSum += x[playerPos + i];
        }
        if(computerSum > playerSum)
            return GameConstants.computer;
        return GameConstants.player;
    }

    /**
     * This method determines the score for a ending board --
     * see GameConstants for scores for win, loss or tie.
     * @param b the board to score
     * @return the score of a board as double
     */
    public double score(Board b){
        if(win(b))
            return GameConstants.win;
        else if(lose(b))
            return GameConstants.lose;
        else
            return GameConstants.tie;
    }

    /**
     * This method determines if the computer is the winner of a game using the
     * X values. It checks if n computer marks have been placed in a column, diagonal or row on a given board.
     * @param b the board to check for winning
     * @return true if computer wins game or false otherwise
     */
    public boolean win(Board b){
        int state[] = b.getBoardState();
        int winCounts = GameConstants.numParams/2;
        for(int i = 1; i <= winCounts; i++)
            if(state[i] == GameConstants.n)
                return true;
        return false;
    }

    /**
     This method determines if the computer lost the game using the
     * X values. It checks if n player marks have been placed in a column, diagonal or row on a given board.
     * @param b the board to check for loss
     * @return true if player wins game or false otherwise
     */
    public boolean lose(Board b){
        int state[] = b.getBoardState();
        int lossCounts = GameConstants.numParams/2;
        for(int i = lossCounts+1; i <= lossCounts*2; i++)
            if(state[i] == GameConstants.n)
                return true;
        return false;
    }

    /**
     This method determines if the game is tied using the
     * X values. It checks if all X values filled
     * @param b the board to check for tie
     * @return true if all X values filled (tie) or false otherwise
     */
    public boolean tie(Board b){
        int state[] = b.getBoardState();
        for(int i = 1; i < GameConstants.numParams; i++)
            if(state[i] == 0) //if at least one slot unfilled
                return false;
        return true;
    }

    /**
     * This is the training function that computes the real value of a board using the successor
     * @param b the board to use to train
     * @return the training score of a board
     */
    public double vTrain(Board b){
        return vHat(successor(b));
    }

    /**
     * This method returns the successor of a board.
     * The last board is its own successor, otherwise, it's 2 steps away
     * @param b the board for which the successor should be returned
     * @return the successor of a board
     */
    public Board successor(Board b){
        int index = (b.getIndex() == gameSteps) ? b.getIndex() : b.getIndex() + 2;
        return experience.getBoard(index);
    }

    /**
     * This method computes vhat of a board, which is represented by the following formula:
     * vhat(b) = w0 + w1x1 + w2x2 + ... + wnxn.
     * If the board is the last board in a game, the fixed score of the board is returned
     * @param b the board for which vhat should be calculated
     * @return the resulting value of the vhat formula for a given board
     */
    public double vHat(Board b){
        double w[] = getW();
        int x[] = b.getBoardState();
        double vhat = w[0];
        if(b.getIndex() == gameSteps)
            vhat = score(b);
        else {
            for (int i = 1; i < x.length; i++)
                vhat += w[i] * x[i];
        }

        return vhat;
    }

    /**
     * This is the caller for the Least Mean Squares (LMS) Method
     * Based on how we store each training game,
     * We call the LMS for each game in order to update the weight for each training example
     */
    public void runLMS(){
        int nGames = experience.getGameCount();
        //loop through each game
        for(int g = 0; g < nGames; g++) {
            experience.setCurrentGame(g);
            setGameSteps(experience.getTrainingExamples().size()-1); //specify number of boards in each game
            LMS();
//            printWeights();
        }
    }

    /**
     * This method uses the Least Mean Squares algorithm to update the weights,
     * which is as follows:
     * for each training example
     *      for each weight wi
     *          update each weight as wi <- wi + eta (vtrain(b) - vhat(b))xi
     */
    public void LMS() {
        double eta = GameConstants.eta;
//        System.out.println("currentGame: " +experience.getCurrentGame());
        ArrayList<Board> trainingExamples = experience.getTrainingExamples();
        int starter = getStarter(trainingExamples.get(0));//who started the game?
//        System.out.println("starter: " +starter);
        int start = (starter == GameConstants.computer)?0:1; //which is the starting board?

        //for each training example
        for (int eg = trainingExamples.size() - 1; eg >= start; eg -= 2) {
            Board b = trainingExamples.get(eg);
            int x[] = b.getBoardState();
            double vT = vTrain(b);
            double vH = vHat(b);
//            System.out.println("vtrain: " +vT + " vhat: " + vH);

            //update the weights
            for (int i = 0; i < w.length; i++)
                w[i] = w[i] + eta * (vT - vH) * x[i];
        }
    }

    public void printWeights(){
//        for(int i=0;i<w.length;i++)
//            System.out.print("  w" + i +"  \t");
//        System.out.println();
        for(int i=0;i<w.length;i++)
            System.out.println("w" + i + ": \t" + Math.round((w[i]*100))/100.0);
//            System.out.print(Math.round((w[i] * 100)) / 100.0 +"\t");
//            System.out.print("w" + i + ": \t" + Math.round((w[i]*100))/100.0);
        System.out.println();
//            System.out.println(String.join(",", Arrays.toString(w)));
    }

}
