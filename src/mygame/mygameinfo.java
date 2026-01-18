package mygame;

import Ex3P.Ex3Algo;
import Ex3P.ManualAlgo;
import exe.ex3.game.PacManAlgo;

public class mygameinfo{
    public static final String MY_ID = "212750947";

    // the game level - between 0 to 4 in level 0 there is 0 ghost in level 2 there is 2 ghost
    // change the number to change scenari0
    public static final int CASE_SCENARIO = 2;

    public static final long RANDOM_SEED = 31;
    public static final boolean CYCLIC_MODE = true;

    public static final int DT = 100;

    public static final PacManAlgo ALGO = new Ex3Algo();
    }

