To compile:

javac -cp ./src/ -d . src/org/fitz/ml/main/PlayTicTacToeGame.java


To Execute:

java org.fitz.ml.main.PlayTicTacToeGame -experience teacher -iterations 1 -file ./data/tic_tac_toe_teacher_games2.txt
 	
    * -experience can be <teacher> or <noteacher>
    * -iterations is an integer representing the number of times
       to train on the same games
    * -file is the path to the training-game file
    * if these parameters are left blank, 
      the game will train for 1 iteration in teacher mode


.-----------------------------------.
| Program files                     |
'-----------------------------------'
.
├── data
│   ├── tic_tac_toe_teacher_games1.txt          --> teacher game file (x-values)  
│   └── tic_tac_toe_teacher_games2.txt          --> teacher game file (raw game board as 2d matrix)  
└── src  
    └── org  
        └── fitz  
            └── ml                              --> machine learning package  
                ├── constants  
                │   └── GameConstants.java      --> contains constants used in the program  
                ├── main                        --> main package  
                │   └── PlayTicTacToeGame.java  --> main class to run the program  
                └── ttt                         --> tic-tac-toe package  
                    ├── Board.java              --> tic-tac-toe-board  
                    ├── Experience.java         --> facilitates teacher or no-teacher experience  
                    ├── Game.java               --> input a move, output win, lose, tie, nothing (not end of game)  
                    ├── Learner.java            --> input experience, output knowledge  
                    ├── Move.java               --> facilitates user and computer moves  
                    ├── Player.java             --> input knowledge and board, output a move  
                    └── TicTacToe.java          --> facilitates testTeacher and testNoTeacher  
