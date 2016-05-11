package org.fitz.ml.ttt;

import org.fitz.ml.constants.GameConstants;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class facilities the selection of a training experience (teacher or no-teacher),
 * parsing of a training game file, and the extraction of
 * experience data to be passed to the learner
 *
 * Created by FitzRoi on 1/19/16.
 */
public class Experience {
    private int experience = 0;
    private ArrayList<ArrayList<Board>> trainingGames;
    private int currentGame;

    public Experience() {
        this.trainingGames = new ArrayList<ArrayList<Board>>();
        this.currentGame = 0;
    }

    public int getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(int currentGame) {
        this.currentGame = currentGame;
    }

    public void selectExperience(int experience) {
        this.experience = experience;
    }

    public int getExperience() {
        return experience;
    }

    public int getGameCount(){
        return trainingGames.size();
    }

    public Board getBoard(int index){
        return trainingGames.get(currentGame).get(index);
    }

    public ArrayList<Board> getTrainingExamples(){
        return trainingGames.get(currentGame);
    }


    /**
     * This method reads the training game file consisting of boards
     * formatted as {'X', ' ' , ' '},{' ', ' ', ' '}, {' ', ' ', ' '}
     * (for teacher mode) and stores games as object of boards.
     * in an arrayList
     * @param filename
     */
    public void parseTrainingFile(String filename) {
        try {
            Path filePath = Paths.get(filename);
            Charset charset = Charset.forName("ISO-8859-1");
            List<String> lines = Files.readAllLines(filePath, charset);
            String bStr[]; //board string
            trainingGames = new ArrayList<ArrayList<Board>>();


            for (int i = 3; i < lines.size(); i++) { //start at 3 based on file format
                //create a new arrayList of boards for each game
                if(lines.get(i).contains(GameConstants.newGame)) {
                    trainingGames.add(new ArrayList<Board>());
                    i++;
                }

                //expected board format per line:
                //{'X', ' ' , ' '},{' ', ' ', ' '},{' ', ' ', ' '}
                bStr = lines.get(i).replace("{","").trim().replace("}","").trim().split(",");
                Board b = new Board();

                for(int j=0; j<bStr.length; j++) {
                    b.placeMark(bStr[j].trim().charAt(1), (j + 1));
                }
                addBoardToGame(b.determineStateFromMatrix());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method reads the training game file (x-values for teacher mode)
     * and stores games as object of boards in an arrayList.
     * @param filename
     */
    public void parseTrainingXFile(String filename) {
        try {
            Path filePath = Paths.get(filename);
            Charset charset = Charset.forName("ISO-8859-1");
            List<String> lines = Files.readAllLines(filePath, charset);
            String bStr[];
            trainingGames = new ArrayList<ArrayList<Board>>();


            for (int i = 4; i < lines.size(); i++) { //start on line 4 based on file format
                //create a new arrayList of boards for each game
                if(lines.get(i).contains(GameConstants.newGame)) {
                    trainingGames.add(new ArrayList<Board>());
                    i++;
                }
                int x[] = new int[GameConstants.numParams]; //the X values (x1...xn)
                x[0] = 1;//init x0 to 1
                bStr = lines.get(i).trim().split("\\s+");

                for (int v = 0; v < bStr.length; v++)
                    x[v + 1] = Integer.parseInt(bStr[v]);

                addBoardToGame(x);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method is used to generate random games for training without a teacher
     */
    public void generateRandomGames(){
        trainingGames = new ArrayList<ArrayList<Board>>();
        
        for(int i = 0; i < GameConstants.nGames; i++){

            Board b = new Board();
            Random rand = new Random();
            int starter = rand.nextInt(2);
            trainingGames.add(new ArrayList<Board>());

            //generate a full random game by choosing a random starter (computer or player)
            while(!b.win() && !b.lose() && !b.tie()){
                if(starter == GameConstants.computer){
                    //make a computer move and add board to set of boards for this game
                    b.makeRandomMove(GameConstants.computerMark);
                    addBoardToGame(b.determineStateFromMatrix());

                    //check if game ended before attempting next play
                    if(b.win() || b.lose() || b.tie())
                        break;

                    //make a player move and add board to set of boards for this game
                    b.makeRandomMove(GameConstants.playerMark);
                    addBoardToGame(b.determineStateFromMatrix());

                }


                if(starter == GameConstants.player){
                    //make a player move and add board to set of boards for this game
                    b.makeRandomMove(GameConstants.playerMark);
                    addBoardToGame(b.determineStateFromMatrix());

                    //check if game ended before attempting next play
                    if(b.win() || b.lose() || b.tie())
                        break;
                    //make a computer move and add board to set of boards for this game
                    b.makeRandomMove(GameConstants.computerMark);
                    addBoardToGame(b.determineStateFromMatrix());

                }

            }
        }

    }

    /**
     * This method adds a board to the current game.
     * @param boardState the state (X values) of a given board
     */
    
    public void addBoardToGame(int boardState[]){
        int boardIndex = trainingGames.get(trainingGames.size()-1).size();
        Board b = new Board(boardIndex, boardState);
        trainingGames.get(trainingGames.size()-1).add(b); //add board to g
    }
    


    /**
     * This method prints the board state or attribute values for each board for a certain game
     * extracted from the training game file
     * @param gameIndex specifies the index of the game in an arraylist of training games
     */
    public void printGameBoard(int gameIndex){
        ArrayList<Board> game = trainingGames.get(gameIndex);
        for(Board b:game){
            for(int i = 0; i < b.getBoardState().length; i++)
                System.out.print(b.getBoardState()[i] +"\t");
            System.out.println();
        }
    }


}
