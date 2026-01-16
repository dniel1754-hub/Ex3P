//package assignments.Ex3;

import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.Arrays;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo {
	private int _count;
	private Map2D _map = null;// לא מאתחלים עם null בבנאי
	private int _lastDir = Game.UP;
	private int BLUE, PINK, GREEN, GRAY;
	private static final int DANGER_DIST = 7;
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
	/**
	 * This ia the main method - that you should design, implement and test.
	 */
	public int move(PacmanGame game) {
		int code = 0;
		int[][] board = game.getGame(code);

		// 1. אתחול יסודי של המפה
		if (_map == null) {
			_map = new Map(board);
			BLUE = Game.getIntColor(Color.BLUE, code);
			PINK = Game.getIntColor(Color.PINK, code);
			GREEN = Game.getIntColor(Color.GREEN, code);
		} else { _map.init(board); }

		// וודא שהמפה מוגדרת כמחזורית לפי הגדרות המשחק
		_map.setCyclic(true);

		Pixel2D me = parsePos(game.getPos(code));
		GhostCL[] ghosts = game.getGhosts(code);

		// 2. חסימה חכמה של בית הרוחות (רק אם אנחנו לא בתוכו!)
		applyGhostHouseBypass(board, me);

		// 3. חישוב מרחקים - אם נכשל, נסה בלי החסימה של בית הרוחות
		Map2D distFromMe = _map.allDistance(me, BLUE);
		if (distFromMe == null) {
			_map.init(board); // איפוס חסימות
			distFromMe = _map.allDistance(me, BLUE);
		}

		// 4. בניית מפת סכנה מרוחות פעילות
		double[][] dangerMap = buildDangerMap(ghosts);

		// 5. בחירת המהלך הטוב ביותר מתוך 4 כיוונים
		int bestDir = -1;
		double bestScore = Double.NEGATIVE_INFINITY;

		for (int dir : new int[]{Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT}) {
			Pixel2D next = neighbor(me, dir);
			if (!isLegal(next, board)) continue;

			// הערכת המהלך: קרבה לאוכל, מרחק מסכנה, והמשכיות
			double score = evaluate(next, board, distFromMe, dangerMap, dir);

			if (score > bestScore) {
				bestScore = score;
				bestDir = dir;
			}
		}

		// 6. הגנה נגד קיפאון - אם לא נמצא מהלך חכם, זוז לכל מקום פנוי
		if (bestDir == -1) {
			for (int d : new int[]{Game.RIGHT, Game.LEFT, Game.UP, Game.DOWN}) {
				if (isLegal(neighbor(me, d), board)) return d;
			}
		}

		_lastDir = bestDir;
		return bestDir;
	}
	/**
	 * פונקציה זו מגדירה את כל אזור בית הרוחות כמכשול (קיר).
	 * היא מחשבת את מרכז הלוח וחוסמת רדיוס מסביבו.
	 */
	private void applyGhostHouseBypass(int[][] board, Pixel2D pacmanPos) {
		int midX = board.length / 2;
		int midY = board[0].length / 2;

		// הגדרת גבולות הריבוע המרכזי (ניתן לשנות את המספרים אם הבית גדול/קטן יותר)
		int radiusX = 4;
		int radiusY = 3;

		for (int x = midX - radiusX; x <= midX + radiusX; x++) {
			for (int y = midY - radiusY; y <= midY + radiusY; y++) {
				Index2D p = new Index2D(x, y);

				// הגנה: לא חוסמים את המשבצת אם הפקמן נמצא עליה כרגע (כדי לא לתקוע אותו)
				if (_map.isInside(p) && !p.equals(pacmanPos)) {
					_map.setPixel(x, y, BLUE);
				}
			}
		}
	}

	private Index2D parsePos(String pos) {
		String[] p = pos.split(",");
		return new Index2D(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
	}

	private double evaluate(Pixel2D pos, int[][] board, Map2D dists, double[][] danger, int dir) {
		double score = 0;
		int x = pos.getX(), y = pos.getY();

		// א. סכנה (עדיפות עליונה)
		double dng = danger[x][y];
		if (dng <= 1.1) return -1000000; // מוות
		score += dng * 500; // ככל שיותר רחוק מהרוח, הציון עולה

		// ב. אוכל - מציאת הנקודה הוורודה הקרובה ביותר מהמיקום הבא
		Pixel2D target = findClosest(board, PINK, dists);
		if (target != null) {
			double d = pos.distance2D(target);
			score += 10000.0 / (d + 1);
		}

		// ג. בונוס על אכילה מיידית
		if (board[x][y] == PINK) score += 2000;

		// ד. מניעת "רעידות" - בונוס על המשך באותו כיוון
		if (dir == _lastDir) score += 100;

		return score;
	}

	private void applySmartBypass(int[][] board, Pixel2D me) {
		int midX = board.length / 2;
		int midY = board[0].length / 2;
		for (int x = midX - 3; x <= midX + 3; x++) {
			for (int y = midY - 2; y <= midY + 2; y++) {
				Index2D p = new Index2D(x, y);
				// חוסם רק אם אני לא עומד שם כרגע (כדי לא להקריס את ה-BFS)
				if (_map.isInside(p) && !p.equals(me)) {
					_map.setPixel(x, y, BLUE);
				}
			}
		}
	}

	private double[][] buildDangerMap(GhostCL[] ghosts) {
		int w = _map.getWidth(), h = _map.getHeight();
		double[][] dMap = new double[w][h];
		for (double[] r : dMap) Arrays.fill(r, 99.0);

		for (GhostCL g : ghosts) {
			if (g.remainTimeAsEatable(0) > 1.5) continue; // התעלמות מרוחות כחולות

			Pixel2D gp = parsePos(g.getPos(0));
			Map2D gDist = _map.allDistance(gp, BLUE);
			if (gDist == null) continue;

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					int d = gDist.getPixel(x, y);
					if (d != -1) dMap[x][y] = Math.min(dMap[x][y], d);
				}
			}
		}
		return dMap;
	}

	private Pixel2D neighbor(Pixel2D p, int dir) {
		int x = p.getX(), y = p.getY();
		if (dir == Game.UP) y++; else if (dir == Game.DOWN) y--;
		else if (dir == Game.LEFT) x--; else if (dir == Game.RIGHT) x++;

		// תמיכה בלוח מחזורי (Wrap-around)
		int w = _map.getWidth(), h = _map.getHeight();
		return new Index2D((x + w) % w, (y + h) % h);
	}

	private boolean isLegal(Pixel2D p, int[][] board) {
		return _map.isInside(p) && board[p.getX()][p.getY()] != BLUE;
	}

	private Pixel2D findClosest(int[][] board, int color, Map2D distMap) {
		Pixel2D best = null; int minD = Integer.MAX_VALUE;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				if (board[x][y] == color) {
					int d = distMap.getPixel(x, y);
					if (d != -1 && d < minD) {
						minD = d; best = new Index2D(x, y);
					}
				}
			}
		}
		return best;
	}

}