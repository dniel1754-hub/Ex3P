//package assignments.Ex3;

import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo{
	private int _count;
	private Map2D _map = null; // לא מאתחלים עם null בבנאי
	private static final int DANGER_DIST = 7;
	private static final int GREEN_DIST = 4;
	public Ex3Algo() {_count=0;}
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
	/**
	 * This ia the main method - that you should design, implement and test.
	 */
	public int move(PacmanGame game) {
		int code = 0;
		int[][] board = game.getGame(code);
		String posStr = game.getPos(code);
		Index2D pacmanPos = parsePos(posStr);
		GhostCL[] ghosts = game.getGhosts(code);

		// אתחול המפה רק בפעם הראשונה או עדכון שלה
		if (_map == null) {
			_map = new Map(board);
		} else {
			_map.init(board);
		}

		// הדפסת דיבאג פעם ב-300 צעדים
		if (_count % 300 == 0) {
			System.out.println("Pacman coordinate: " + posStr);
		}
		_count++;

		// 1. זיהוי סכנה (רוחות)
		GhostCL closestGhost = findClosestGhost(pacmanPos, ghosts);
		double distToGhost = (closestGhost != null) ? pacmanPos.distance2D(parsePos(closestGhost.getPos(0))) : Double.MAX_VALUE;

		// א. בריחה מרוח מסוכנת
		if (closestGhost != null && closestGhost.remainTimeAsEatable(0) <= 0 && distToGhost < DANGER_DIST) {
			return getFleeMove(game, pacmanPos, ghosts);
		}

		// ב. בדיקת נקודה ירוקה קרובה
		int green = Game.getIntColor(Color.GREEN, code);
		Index2D greenPoint = findClosestColor(board, green, pacmanPos);
		if (greenPoint != null && pacmanPos.distance2D(greenPoint) <= GREEN_DIST) {
			return getMoveTowards(pacmanPos, greenPoint);
		}

		// ג. ציד רוחות כחולות (אכילות)
		if (closestGhost != null && closestGhost.remainTimeAsEatable(0) > distToGhost) {
			return getMoveTowards(pacmanPos, parsePos(closestGhost.getPos(0)));
		}

		// ד. יעד ברירת מחדl: נקודה ורודה
		int pink = Game.getIntColor(Color.PINK, code);
		Index2D pinkPoint = findClosestColor(board, pink, pacmanPos);
		if (pinkPoint != null) {
			return getMoveTowards(pacmanPos, pinkPoint);
		}

		return Game.UP; // אם אין מה לעשות
	}
	private int getFleeMove(PacmanGame game, Index2D p, GhostCL[] ghosts) {
		int bestDir = -1;
		double maxDist = -1;
		int[] dirs = {Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT};

		for (int dir : dirs) {
			Index2D next = getNext(p, dir);
			if (_map.isInside(next) && boardAt(next, game) != Game.getIntColor(Color.BLACK, 0)) {
				double minD = Double.MAX_VALUE;
				for (GhostCL g : ghosts) {
					minD = Math.min(minD, next.distance2D(parsePos(g.getPos(0))));
				}
				if (minD > maxDist) {
					maxDist = minD;
					bestDir = dir;
				}
			}
		}
		return bestDir;
	}
	private int boardAt(Index2D p, PacmanGame game) {
		return game.getGame(0)[p.getX()][p.getY()];
	}

	private Index2D getNext(Index2D p, int dir) {
		if (dir == Game.DOWN) return new Index2D(p.getX(), p.getY() + 1);
		if (dir == Game.UP) return new Index2D(p.getX(), p.getY() - 1);
		if (dir == Game.LEFT) return new Index2D(p.getX() - 1, p.getY());
		if (dir == Game.RIGHT) return new Index2D(p.getX() + 1, p.getY());
		return p;
	}

	public int getMoveTowards(Index2D src, Index2D dest) {
		int black = Game.getIntColor(Color.BLACK, 0);
		Pixel2D[] path = _map.shortestPath(src, dest, black);
		if (path != null && path.length > 1) {
			return calculateDirection(src, new Index2D(path[1]));
		}
		return Game.UP;
	}

	private int calculateDirection(Index2D src, Index2D dest) {
		int dx = dest.getX() - src.getX();
		int dy = dest.getY() - src.getY();
		if (dx == 1) return Game.RIGHT;
		if (dx == -1) return Game.LEFT;
		if (dy == 1) return Game.UP;
		if (dy == -1) return Game.DOWN;
		return -1;
	}

	private GhostCL findClosestGhost(Index2D p, GhostCL[] ghosts) {
		GhostCL closest = null;
		double minD = Double.MAX_VALUE;
		for (GhostCL g : ghosts) {
			double d = p.distance2D(parsePos(g.getPos(0)));
			if (d < minD) {
				minD = d;
				closest = g;
			}
		}
		return closest;
	}

	private Index2D findClosestColor(int[][] board, int color, Index2D pos) {
		Index2D res = null;
		double minD = Double.MAX_VALUE;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				if (board[x][y] == color) {
					double d = pos.distance2D(new Index2D(x, y));
					if (d < minD) {
						minD = d;
						res = new Index2D(x, y);
					}
				}
			}
		}
		return res;
	}

	private Index2D parsePos(String pos) {
		String[] p = pos.split(",");
		return new Index2D(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
	}



	private static void printBoard(int[][] b) {
		for(int y =0;y<b[0].length;y++){
			for(int x =0;x<b.length;x++){
				int v = b[x][y];
				System.out.print(v+"\t");
			}
			System.out.println();
		}
	}
	private static void printGhosts(GhostCL[] gs) {
		for(int i=0;i<gs.length;i++){
			GhostCL g = gs[i];
			System.out.println(i+") status: "+g.getStatus()+",  type: "+g.getType()+",  pos: "+g.getPos(0)+",  time: "+g.remainTimeAsEatable(0));
		}
	}
	private static int randomDir() {
		int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
		int ind = (int)(Math.random()*dirs.length);
		return dirs[ind];
	}
}