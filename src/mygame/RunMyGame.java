package mygame;

import Ex3P.Ex3Algo;
import exe.ex3.game.PacManAlgo;

public class RunMyGame {
    public static void main(String[] args) {
        mygame game = new mygame();


        game.init(mygameinfo.CASE_SCENARIO, null, mygameinfo.CYCLIC_MODE, mygameinfo.RANDOM_SEED, 0, 0, 0);

        int[][] board = game.getGame(0);
        MyGameGUI.init(board.length, board[0].length);

        PacManAlgo algo = mygameinfo.ALGO;

        while (game.getStatus() == 0) {
            MyGameGUI.draw(game);

            int dir = 0;
            if(algo != null) {
                dir = algo.move(game);
            }

            game.move(dir);

            try {
                Thread.sleep(mygameinfo.DT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        MyGameGUI.draw(game);
        System.out.println("Game Over. Final Score: " + game.getData(0));
    }
}