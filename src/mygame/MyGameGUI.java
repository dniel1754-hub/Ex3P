package mygame;

import Ex3P.StdDraw;
import exe.ex3.game.GhostCL;
import java.awt.Color;
import java.awt.Font;

public class MyGameGUI {

    public static void init(int w, int h) {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(0, w);
        StdDraw.setYscale(0, h);
        StdDraw.enableDoubleBuffering();
    }

    public static void draw(mygame game) {
        StdDraw.clear(Color.BLACK);

        int[][] board = game.getGame(0);
        int w = board.length;
        int h = board[0].length;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int cell = board[x][y];
                if (cell == mygame.WALL_COLOR) {
                    StdDraw.setPenColor(new Color(0, 0, 200));
                    StdDraw.square(x + 0.5, y + 0.5, 0.5);
                    StdDraw.setPenColor(new Color(100, 100, 255));
                    StdDraw.square(x + 0.5, y + 0.5, 0.5);
                } else if (cell == mygame.FOOD_COLOR) {
                    StdDraw.setPenColor(new Color(255, 180, 180));
                    StdDraw.filledCircle(x + 0.5, y + 0.5, 0.15);
                } else if (cell == mygame.POWER_COLOR) {
                    StdDraw.setPenColor(Color.GREEN);
                    StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
                }
            }
        }

        String[] pPos = game.getPos(0).split(",");
        double px = Double.parseDouble(pPos[0]) + 0.5;
        double py = Double.parseDouble(pPos[1]) + 0.5;

        try {
            StdDraw.picture(px, py, "images/p1.png", 1.0, 1.0);
        } catch (Exception e) {
            try {
                StdDraw.picture(px, py, "src/images/p1.png", 1.0, 1.0);
            } catch (Exception e2) {
                StdDraw.setPenColor(Color.YELLOW);
                StdDraw.filledCircle(px, py, 0.4);
            }
        }

        GhostCL[] ghosts = game.getGhosts(0);
        for (int i = 0; i < ghosts.length; i++) {
            GhostCL g = ghosts[i];
            String[] gPos = g.getPos(0).split(",");
            double gx = Double.parseDouble(gPos[0]) + 0.5;
            double gy = Double.parseDouble(gPos[1]) + 0.5;

            boolean isEatable = g.remainTimeAsEatable(0) > 0;
            String imgName = "g" + (i % 4) + ".png";

            try {
                if (isEatable) {
                    StdDraw.setPenColor(Color.CYAN);
                    StdDraw.filledCircle(gx, gy, 0.45);
                    try {
                        StdDraw.picture(gx, gy, "images/" + imgName, 0.7, 0.7);
                    } catch (Exception ex) {
                        StdDraw.picture(gx, gy, "src/images/" + imgName, 0.7, 0.7);
                    }
                } else {
                    try {
                        StdDraw.picture(gx, gy, "images/" + imgName, 0.9, 0.9);
                    } catch (Exception ex) {
                        StdDraw.picture(gx, gy, "src/images/" + imgName, 0.9, 0.9);
                    }
                }
            } catch (Exception e) {
                StdDraw.setPenColor(isEatable ? Color.CYAN : Color.RED);
                StdDraw.filledCircle(gx, gy, 0.4);
            }
        }

        if (game.getStatus() != 0) {
            StdDraw.setPenColor(new Color(0, 0, 0, 180));
            StdDraw.filledRectangle(w/2.0, h/2.0, w/2.0, h/2.0);

            String msg = game.end(0);

            if (msg.contains("Victory")) {
                StdDraw.setPenColor(Color.GREEN);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 60));
                StdDraw.text(w/2.0, h/2.0 + 2, "VICTORY!");
            } else {
                StdDraw.setPenColor(Color.RED);
                StdDraw.setFont(new Font("Arial", Font.BOLD, 60));
                StdDraw.text(w/2.0, h/2.0 + 2, "GAME OVER");
            }

            StdDraw.setPenColor(Color.WHITE);
            StdDraw.setFont(new Font("Arial", Font.PLAIN, 30));
            String score = game.move(0);
            if(score.contains("Victory")) score = "Final Score: " + game.getData(0);
            else score = score.replace("Score: ", "Final Score: ");

            StdDraw.text(w/2.0, h/2.0 - 2, score);
        }

        StdDraw.show();
    }
}