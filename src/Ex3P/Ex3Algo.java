package Ex3P;

import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 * <p>
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo {
    private int _count;
    private Map _map;
    private int _lastDir = Game.UP;
    private int BLUE, PINK, GREEN, GRAY;
    private static final int DANGER_RADIUS = 7;
    private static final int GREEN_DIST = 4;
    private static final int HUNT_BUFFER = 3;


    public Ex3Algo() {
        _count = 0;
    }

    @Override
    /**
     *  Add a short description for the algorithm as a String.
     */
    public String getInfo() {
        String ans = "this function does the pacman algorithem - level 0 - if there arent any monsters alive and whileexist a pink point do alldistance for the map and do shortestpath" +
                " to the closest pink pointif there is a monster exist - do all distance for her location aad all the" +
                " maps (of all distance for each monster ) if min of the location of all monsters active is les then 7 -for the " +
                "closes  go to one of the neighbors (with get neighbors function) that his distance for the closest monster is the biggestif there is a green  foint in distance of 4 or less and shortest path to her is in places equals or bigger tothe closest monster - go to the green point and if a monster is closest than the time that remains (we can reach to a monster in the left time) go to the monster and eat her ( do not enter to their home anyway)   ";
        return ans;
    }

    @Override

    // This is the main method that calculates the best next move for Pacman based on danger, food, and future score.

    public int move(PacmanGame game) {
        int pac = 0;
        int[][] board = game.getGame(pac);
        if (_map == null) {
            _map = new Map(board);
        } else {
            _map = new Map(board);
        }
        _map.setCyclic(GameInfo.CYCLIC_MODE);
        Pixel2D me = parsePos(game.getPos(pac));

        if (_count == 0) {
            BLUE = Game.getIntColor(Color.BLUE, 0);
            PINK = Game.getIntColor(Color.PINK, 0);
            GREEN = Game.getIntColor(Color.GREEN, 0);
        }

        GhostCL[] ghosts = game.getGhosts(pac);
        double[][] mapdanger = DangerMaptonearest((Map) _map, board, ghosts);

        int bestDir5 = -1;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int dir : new int[]{Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT}) {
            Pixel2D nextTo = cheksneighbor(me, dir, (Map) _map);
            if (!isLegaltomove(nextTo, board)) continue;

            double ans1 = evaluatedir(nextTo, (Map) _map, board, mapdanger);

            Map2D d2 = _map.allDistance(nextTo, BLUE);
            ans1 += 0.5 * futureEstimate(d2, board, mapdanger);

            if (dir == _lastDir) ans1 += 500;

            if (ans1 > bestScore) {
                bestScore = ans1;
                bestDir5 = dir;
            }
        }

        if (bestDir5 == -1) {
            for (int i = 0; i < 4; i++) if (isLegaltomove(cheksneighbor(me, i, (Map) _map), board)) return i;
        }

        _lastDir = bestDir5;
        _count++;
        return bestDir5;
    }

    // Parses a string coordinate "x,y" into an Index2D object.
    private Index2D parsePos(String pos) {
        String[] p = pos.split(",");
        return new Index2D(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
    }

    // Constructs a 2D array representing the distance to the nearest dangerous ghost for each cell on the board.
    public double[][] DangerMaptonearest(Map map, int[][] board, GhostCL[] ghosts) {
        int uy = board.length,
                high = board[0].length;
        double[][] danger = new double[uy][high];
        for (double[] r : danger) Arrays.fill(r, 99.0);

        if (ghosts != null && ghosts.length > 0) {
            for (int i = 0; i < ghosts.length; i++) {
                if (ghosts[i].remainTimeAsEatable(i) > 3.0) continue;
                Pixel2D gp = parsePos(ghosts[i].getPos(i));
                Map2D dist = _map.allDistance(gp, BLUE);
                for (int x = 0; x < uy; x++) {
                    for (int y = 0; y < high; y++) {
                        double dis = dist.getPixel(x, y);
                        if (dis != -1) danger[x][y] = Math.min(danger[x][y], dis);
                    }
                }
            }
        } else {
            for (int x = 0; x < uy; x++) {
                for (int y = 0; y < high; y++) {
                    if (board[x][y] < 0) {
                        danger[x][y] = 0;
                    }
                }
            }
        }

        return danger;
    }

    // Evaluates the safety and potential score of a specific position based on ghost proximity, safe space, and food
    public double evaluatedir(Pixel2D pos, Map map, int[][] board, double[][] isdanger) {
        double ans = 0;
        int x = pos.getX(), y = pos.getY();
        double ghostDist11 = isdanger[x][y];

        if (ghostDist11 <= 1.1) return -10000000.0;
        if (ghostDist11 <= 2.1) ans -= 500000.0;
        if (ghostDist11 <= 3.1) ans -= 100000.0;

        int gotosafeSpace = calculatSafeSpace(pos, map, board, isdanger, 15);
        ans += gotosafeSpace * 2000;

        Map2D distMap = _map.allDistance(pos, BLUE);
        Pixel2D pink = closestPINK(board, distMap, PINK);

        if (pink != null) {
            double d = distMap.getPixel(pink.getX(), pink.getY());
            ans += 200000.0 / (d + 1);
        } else {
            ans += ghostDist11 * 5000;
        }

        if (board[x][y] == PINK) ans += 10000;

        return ans;
    }

    // Calculates the number of safe reachable tiles from a starting position using BFS, avoiding immediate danger.
    public <Queue> int calculatSafeSpace(Pixel2D start, Map map, int[][] board, double[][] danger, int border) {
        java.util.Queue<Pixel2D> q = new java.util.LinkedList<>();
        ;
        java.util.Map<String, Integer> dist = new HashMap<>();
        q.add(start);
        dist.put(keytohash(start), 0);
        int count = 0;
        while (!q.isEmpty() && count < border) {
            Pixel2D curent = q.poll();
            int d = dist.get(keytohash(curent));
            count++;
            for (int dir : new int[]{0, 1, 2, 3}) {
                Pixel2D yr = cheksneighbor(curent, dir, map);
                if (!isLegaltomove(yr, board) || dist.containsKey(keytohash(yr))) continue;
                if (danger[yr.getX()][yr.getY()] <= d + 1) continue;
                dist.put(keytohash(yr), d + 1);
                q.add(yr);
            }
        }
        return count;
    }

    // Estimates the future potential of a position by analyzing the density and proximity of nearby food pellets.
    public double futureEstimate(Map2D distance, int[][] board, double[][] danger) {
        double bestdir = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == PINK) {
                    double d = distance.getPixel(i, j);
                    if (d != -1 && danger[i][j] > d + 2) {
                        bestdir = Math.max(bestdir, 100000 / (d + 1));
                    }
                }
            }
        }
        return bestdir;
    }

    // Finds the nearest pixel of a specific target color (like pink food dots) from the current distance map.
    public Pixel2D closestPINK(int[][] board, Map2D dist, int color) {
        Pixel2D Rbest = null;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (board[i][j] == color && dist.getPixel(i, j) != -1 && dist.getPixel(i, j) < min) {
                    min = dist.getPixel(i, j);
                    Rbest = new Index2D(i, j);
                }
        return Rbest;
    }

    // Computes the coordinate of a neighboring cell in a given direction, handling cyclic map boundaries.
    public Pixel2D cheksneighbor(Pixel2D p, int direct, Map map) {
        int x = p.getX(), y = p.getY();
        if (direct == Game.UP) y++;
        else if (direct == Game.DOWN) y--;
        else if (direct == Game.LEFT) x--;
        else if (direct == Game.RIGHT) x++;
        int w = _map.getMap().length, h = _map.getMap()[0].length;
        return new Index2D((x + w) % w, (y + h) % h);
    }

    // Checks if a given position is a valid move (within bounds, not a wall, and not inside the ghost house).
    public boolean isLegaltomove(Pixel2D p, int[][] board) {
        if (p.getX() < 0 || p.getX() >= board.length || p.getY() < 0 || p.getY() >= board[0].length) return false;
        return board[p.getX()][p.getY()] != BLUE && !WithinGhostHouse(p, board);
    }

    // Determines if a specific pixel is located within the designated ghost spawn area (ghost house).
    public boolean WithinGhostHouse(Pixel2D p, int[][] board) {
        int mx = board.length / 2, my = board[0].length / 2;
        return Math.abs(p.getX() - mx) < 3 && Math.abs(p.getY() - my) < 3 && board[p.getX()][p.getY()] == 0;
    }

    // Generates a unique string key for a Pixel2D object to be used in hash maps.
    public String keytohash(Pixel2D p) {
        return p.getX() + "," + p.getY();
    }
}
