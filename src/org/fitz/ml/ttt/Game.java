package org.fitz.ml.ttt;

/**
 * Created by FitzRoi on 1/19/16.
 */
public class Game {
    private Board b;
    public Game(){b = new Board();}

    public boolean gameEnded(Board b){
        setBoard(b);
        if (win()) {
            b.printMatrix();
            System.out.println("Computer Win!!!");
            return true;
        } else if (lose()) {
            b.printMatrix();
            System.out.println("You Win!!!");
            return true;
        } else if (tie()) {
            b.printMatrix();
            System.out.println("Tie!!");
            return true;
        } else{
            return false;
        }

    }

    public void setBoard(Board b){this.b = b;}

    public boolean win() {
        return b.win();
    }

    public boolean lose() {
        return b.lose();
    }

    public boolean tie() {
        return b.tie();
    }


}
