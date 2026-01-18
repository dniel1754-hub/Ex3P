package mygame;

import exe.ex3.game.GhostCL;

public interface PacmanGame {
    int INIT = 0;
    int PLAY = 1;
    int PAUSE = 2;
    int DONE = 3;
    int ERR = -1;
    int STAY = 0;
    int LEFT = 2;
    int RIGHT = 4;
    int UP = 1;
    int DOWN = 3;

    Character getKeyChar();

    String getPos(int var1);

    GhostCL[] getGhosts(int var1);

    int[][] getGame(int var1);

    String move(int var1);

    void play();

    String end(int var1);

    String getData(int var1);

    int getStatus();

    boolean isCyclic();

    String init(int var1, String var2, boolean var3, long var4, double var6, int var8, int var9);
}

