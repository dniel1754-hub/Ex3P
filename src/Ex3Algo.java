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
	/**
	 * This ia the main method - that you should design, implement and test.
	 */
	public int move(PacmanGame game) {
		int code = 0;
		int[][] board = game.getGame(code);

		// יצירת מפה נקייה בכל צעד כדי למנוע באגים של "לכלוך" המפה
		Map map = new Map(board);
		map.setCyclic(true); // עקרון מהדוגמה השנייה: תמיכה במחזוריות

		BLUE = Game.getIntColor(Color.BLUE, code);
		PINK = Game.getIntColor(Color.PINK, code);
		GREEN = Game.getIntColor(Color.GREEN, code);

		Pixel2D me = parsePos(game.getPos(code));
		GhostCL[] ghosts = game.getGhosts(code);

		// 1. הגדרת גבולות בית הרוחות (7x4 במרכז)
		int midX = board.length / 2;
		int midY = board[0].length / 2;
		Pixel2D houseP1 = new Index2D(midX - 3, midY - 2);
		Pixel2D houseP2 = new Index2D(midX + 3, midY + 1);

		// 2. חישוב מרחקים נקי (עקרון מהדוגמה הראשונה: תמיד עובד כי המפה לא חסומה)
		Map2D distFromMe = map.allDistance(me, BLUE);

		// 3. בניית מפת סכנה (רק מרוחות פעילות ומחוץ לבית)
		double[][] dangerMap = buildDangerMap(map, board, ghosts, houseP1, houseP2);

		// 4. לולאת קבלת החלטות (עקרון מהדוגמה השנייה: בדיקת כל הכיוונים)
		int bestDir = -1;
		double bestScore = Double.NEGATIVE_INFINITY;

		for (int dir : new int[]{Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT}) {
			Pixel2D next = neighbor(me, dir, map); // שימוש בפונקציית neighbor מחזורית

			// --- בדיקות חוקיות (החלק הקריטי למניעת תקיעה) ---

			// א. האם זה קיר?
			if (!map.isInside(next) || board[next.getX()][next.getY()] == BLUE) continue;

			// ב. חסימת בית הרוחות (לוגית בלבד!)
			// נכנסים רק אם אנחנו כבר בטעות בפנים וצריכים לצאת
			if (isInsideRect(next, houseP1, houseP2) && !isInsideRect(me, houseP1, houseP2)) {
				continue; // מדלגים על המהלך הזה - הוא מוביל לבית הרוחות
			}

			// ג. חישוב ניקוד
			double score = evaluate(next, board, distFromMe, dangerMap, dir);

			if (score > bestScore) {
				bestScore = score;
				bestDir = dir;
			}
		}

		// 5. מנגנון אל-כשל (עקרון מהדוגמה הראשונה: validMove)
		// אם הכל נכשל, פשוט זוז לכל משבצת פנויה כדי לא לקפוא
		if (bestDir == -1) {
			return getAnyValidMove(me, map, board);
		}

		_lastDir = bestDir;
		return bestDir;
	}

	/**
	 * פונקציית הניקוד (שילוב של הדוגמאות):
	 * - בריחה רק אם המרחק < 7.
	 * - עדיפות לאוכל.
	 * - בונוס יציבות.
	 */
	private double evaluate(Pixel2D pos, int[][] board, Map2D dists, double[][] danger, int dir) {
		double score = 0;
		int x = pos.getX(), y = pos.getY();

		// סכנה: בריחה רק אם הרוח קרובה מ-7 צעדים
		double dng = danger[x][y];
		if (dng <= 1.1) return -10000000; // מוות ודאי
		if (dng <= DANGER_RADIUS) {
			// ככל שמתקרבים ל-0, העונש גדל משמעותית
			score -= (DANGER_RADIUS - dng) * 100000;
		}

		// אוכל: התקרבות לנקודה ורודה (באמצעות BFS מהדוגמה השנייה)
		if (dists != null) {
			Pixel2D target = findClosest(board, PINK, dists);
			if (target != null) {
				// אומדן מרחק ליעד
				double d = pos.distance2D(target);
				score += 5000.0 / (d + 1);
			}
		}

		// בונוסים מקומיים
		if (board[x][y] == PINK) score += 2000;
		if (board[x][y] == GREEN) score += 500;
		if (dir == _lastDir) score += 50; // מניעת רעידות

		return score;
	}

	/**
	 * בונה מפת סכנה. מתעלמת מרוחות בתוך הבית (כדי לעבור ליד הקירות בבטחה).
	 */
	private double[][] buildDangerMap(Map map, int[][] board, GhostCL[] ghosts, Pixel2D p1, Pixel2D p2) {
		int w = board.length, h = board[0].length;
		double[][] dMap = new double[w][h];
		for (double[] r : dMap) java.util.Arrays.fill(r, 99.0);

		for (GhostCL g : ghosts) {
			Pixel2D gp = parsePos(g.getPos(0));

			// סינון רוחות (עקרון מהדוגמה הראשונה):
			// 1. אם הרוח אכילה - לא בורחים.
			// 2. אם הרוח בתוך הבית - לא בורחים (היא כלואה).
			if (g.remainTimeAsEatable(0) > 1.0 || isInsideRect(gp, p1, p2)) continue;

			// חישוב מרחק BFS אמיתי (דרך הקירות הפתוחים)
			Map2D gDist = map.allDistance(gp, BLUE);
			if (gDist == null) continue;

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					int d = gDist.getPixel(x, y);
					if (d != -1) {
						dMap[x][y] = Math.min(dMap[x][y], d);
					}
				}
			}
		}
		return dMap;
	}

	// --- פונקציות עזר מהדוגמאות ---

	private int getAnyValidMove(Pixel2D me, Map map, int[][] board) {
		for (int d : new int[]{Game.UP, Game.RIGHT, Game.DOWN, Game.LEFT}) {
			Pixel2D n = neighbor(me, d, map);
			if (map.isInside(n) && board[n.getX()][n.getY()] != BLUE) return d;
		}
		return Game.UP;
	}

	private boolean isInsideRect(Pixel2D p, Pixel2D p1, Pixel2D p2) {
		int minX = Math.min(p1.getX(), p2.getX());
		int maxX = Math.max(p1.getX(), p2.getX());
		int minY = Math.min(p1.getY(), p2.getY());
		int maxY = Math.max(p1.getY(), p2.getY());
		return p.getX() >= minX && p.getX() <= maxX && p.getY() >= minY && p.getY() <= maxY;
	}

	private Pixel2D neighbor(Pixel2D p, int dir, Map map) {
		int x = p.getX(), y = p.getY();
		if (dir == Game.UP) y++; else if (dir == Game.DOWN) y--;
		else if (dir == Game.LEFT) x--; else if (dir == Game.RIGHT) x++;
		int w = map.getWidth(), h = map.getHeight();
		return new Index2D((x + w) % w, (y + h) % h); // מחזוריות
	}

	private Pixel2D findClosest(int[][] board, int color, Map2D distMap) {
		if (distMap == null) return null;
		Pixel2D best = null; int minD = Integer.MAX_VALUE;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				if (board[x][y] == color) {
					int d = distMap.getPixel(x, y);
					if (d != -1 && d < minD) { minD = d; best = new Index2D(x, y); }
				}
			}
		}
		return best;
	}

	private Index2D parsePos(String pos) {
		String[] p = pos.split(",");
		return new Index2D(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
	}
}