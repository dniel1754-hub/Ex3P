package mygame;



public interface GhostCL {
    int INIT = 0;
    int PLAY = 1;
    int PAUSE = 2;
    int RANDOM_WALK0 = 10;
    int RANDOM_WALK1 = 11;
    int GREEDY_SP = 12;

    int getType();

    String getPos(int var1);

    String getInfo();

    double remainTimeAsEatable(int var1);

    int getStatus();
}
