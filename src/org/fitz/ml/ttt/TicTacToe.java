package org.fitz.ml.ttt;

import org.fitz.ml.constants.GameConstants;
import java.util.Scanner;

/** This class is used to run the tic-tic-toe game based on teacher or noteacher modes
 * Created by FitzRoi on 1/19/16.
 */
public class TicTacToe {
    private int iterations;
    public String continueGame = "Y";

    public TicTacToe(int iterations){this.iterations = iterations;}

    /**
     * This method allows a user to test the tic-tac-toe-game by running in teacher mode
     * @param trainingFile file of games
     */
    public void testTeacher(String trainingFile){
        System.out.println("TIC-TAC-TOE TEACHER MODE");
        System.out.println("TRAINING FILE: " +trainingFile);
        System.out.println("ITERATIONS: " +iterations);

        Experience E = new Experience();
        E.parseTrainingFile(trainingFile);
        Learner L = new Learner(E);


        System.out.println("\nInitial Random Weights");
        L.initWeights();
        L.printWeights();

        for(int i = 0; i < iterations; i++)
            L.runLMS();

        System.out.println("Updated Weights");
        L.printWeights();

        Scanner scanner = new Scanner(System.in);

        //keep playing until user stops
        while(continueGame.equalsIgnoreCase("y")) {
            Player P = new Player(L, new Board());
            play(P);
            System.out.println("\nContinue (Y/N)? ");
            continueGame = scanner.next();
        }
    }

    /**
     * This method allows a user to test the tic-tac-toe-game by running in teacher mode
     */
    public void testNoTeacher(){

        System.out.println("TIC-TAC-TOE NO-TEACHER MODE");
        System.out.println("ITERATIONS: " +iterations);

        Experience E = new Experience();
        E.generateRandomGames();
        Learner L = new Learner(E);


        System.out.println("Initial Random Weights");
        L.initWeights();
        L.printWeights();

        for(int i = 0; i < iterations; i++)
            L.runLMS();

        System.out.println("Updated Weights");
        L.printWeights();


        Scanner scanner = new Scanner(System.in);

        //keep playing until user stops
        while(continueGame.equalsIgnoreCase("y")) {
            Player P = new Player(L, new Board());
            play(P);
            System.out.println("\nContinue (Y/N): ");
            continueGame = scanner.next();
        }
    }

    /**
     * This method allows a user to play the tic-tac-toe game vs the computer by making moves.
     * The user selects who starts the game and continues playing until win/loss/tie
     * @param player facilitates the input of knowledge from learner and the output of a move
     */
    public void play(Player player){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose starter (1: computer; 0: player): ");
        int starter = scanner.nextInt();
        Game game = new Game();
        Board b = player.getBoard();

        //if the computer starts the game
        if(starter == GameConstants.computer) {
            while (!game.win() && !game.lose() && !game.tie()) {
                //computer makes move first
                Move computerMove = player.computerMove();
                if (computerMove != null) {
                    boolean placed = b.placeMark(GameConstants.computerMark, computerMove.getRow(), computerMove.getCol());
                    if(placed) {
                        System.out.println("Computer moved to position " + computerMove.getPosNumber());
                    }
                    b.setBoardState(b.determineStateFromMatrix());
                    player.setBoard(b);
                    if (game.gameEnded(b))
                        break;

                }
                //next, player makes move
                Move playerMove = player.playerMove();
                if (playerMove != null) {
                    boolean userPlaced = b.placeMark(GameConstants.playerMark, playerMove.getPosNumber());
                    if (!userPlaced) {
                        System.out.println("Error! Could not make move at position " + playerMove.getPosNumber());
                    }
                    b.setBoardState(b.determineStateFromMatrix());
                    player.setBoard(b);
                    if (game.gameEnded(b))
                        break;
                }


            }
        }

        //if the user starts the game
        else if(starter == GameConstants.player) {
            while (!game.win() && !game.lose() && !game.tie()) {
                //player makes move first
                Move playerMove = player.playerMove();
                if (playerMove != null) {
                    boolean userPlaced = b.placeMark(GameConstants.playerMark, playerMove.getPosNumber());
                    if (!userPlaced) {
                        System.out.println("Error! Could not make move at position " + playerMove.getPosNumber());
                    }
                    b.setBoardState(b.determineStateFromMatrix());
                    player.setBoard(b);
                    if (game.gameEnded(b))
                        break;
                }

                //next, computer makes move
                Move computerMove = player.computerMove();
                if (computerMove != null) {
                    boolean placed = b.placeMark(GameConstants.computerMark, computerMove.getRow(), computerMove.getCol());
                    if(placed) {
                        System.out.println("Computer moved to position " + computerMove.getPosNumber());
                    }
                    b.setBoardState(b.determineStateFromMatrix());
                    player.setBoard(b);
                    if (game.gameEnded(b))
                        break;
                }

            }
        }
    }

}
