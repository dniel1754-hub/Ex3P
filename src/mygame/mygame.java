package mygame;

import Ex3P.Index2D;
import Ex3P.Pixel2D;
import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacmanGame;

import java.awt.Color;
import java.util.ArrayList;

public class mygame implements PacmanGame {
    private int[][] _board;
    private int _width = 20;
    private int _height = 20;
    private Pixel2D _playerPos;
    private ArrayList<GhostData> _ghosts;
    private int _score;
    private long _seed;
    private boolean _gameOver = false;
    private boolean _victory = false;
    private int _foodCount = 0;

    public static final int WALL_COLOR = Game.getIntColor(Color.BLUE, 0);
    public static final int FOOD_COLOR = Game.getIntColor(Color.PINK, 0);
    public static final int POWER_COLOR = Game.getIntColor(Color.GREEN, 0);
    public static final int EMPTY_COLOR = 0;

    // Default constructor for the game.
    public mygame() {
    }

    // Initializes the game, resets score and ghosts, and builds the map based on the level.
    @Override
    public String init(int level, String file, boolean cyclic, long seed, double v, int i1, int i2) {
        this._seed = seed;
        this._score = 0;
        this._gameOver = false;
        this._victory = false;
        this._foodCount = 0;
        this._ghosts = new ArrayList<>();
        this._board = new int[_width][_height];

        initManualMap(level);
        return "Game Initialized Level: " + level;
    }

    // Manually constructs the board walls, food, and spawns ghosts according to the selected level.
    private void initManualMap(int level) {
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                _board[x][y] = FOOD_COLOR;
            }
        }
        for (int i = 0; i < _width; i++) {
            _board[i][0] = WALL_COLOR; _board[i][_height - 1] = WALL_COLOR;
        }
        for (int i = 0; i < _height; i++) {
            _board[0][i] = WALL_COLOR; _board[_width - 1][i] = WALL_COLOR;
        }

        for (int y = 4; y < 16; y++) _board[4][y] = WALL_COLOR;
        for (int y = 4; y < 16; y++) _board[15][y] = WALL_COLOR;
        _board[7][15] = WALL_COLOR; _board[8][15] = WALL_COLOR;
        _board[11][15] = WALL_COLOR; _board[12][15] = WALL_COLOR;
        _board[7][4] = WALL_COLOR; _board[8][4] = WALL_COLOR;
        _board[11][4] = WALL_COLOR; _board[12][4] = WALL_COLOR;

        int cx = _width / 2;
        int cy = _height / 2;
        _board[cx-2][cy-1] = WALL_COLOR; _board[cx-2][cy] = WALL_COLOR; _board[cx-2][cy+1] = WALL_COLOR;
        _board[cx+2][cy-1] = WALL_COLOR; _board[cx+2][cy] = WALL_COLOR; _board[cx+2][cy+1] = WALL_COLOR;
        _board[cx-1][cy-1] = WALL_COLOR; _board[cx][cy-1] = WALL_COLOR; _board[cx+1][cy-1] = WALL_COLOR;
        _board[cx-1][cy+1] = WALL_COLOR; _board[cx+1][cy+1] = WALL_COLOR;

        for(int x=cx-1; x<=cx+1; x++) {
            for(int y=cy-1; y<=cy+1; y++) {
                if(_board[x][y] != WALL_COLOR) _board[x][y] = EMPTY_COLOR;
            }
        }
        _board[cx][cy+1] = EMPTY_COLOR;
        _board[cx][cy+2] = EMPTY_COLOR;

        _board[1][1] = POWER_COLOR;
        _board[_width - 2][1] = POWER_COLOR;
        _board[1][_height - 2] = POWER_COLOR;
        _board[_width - 2][_height - 2] = POWER_COLOR;

        _playerPos = new Index2D(1, 2);
        _board[1][2] = EMPTY_COLOR;

        int ghostsToCreate = Math.max(0, Math.min(level, 4));

        int[][] startPos = {
                {0, 0},
                {-1, 0},
                {1, 0},
                {0, -1}
        };

        for (int i = 0; i < ghostsToCreate; i++) {
            int gx = cx + startPos[i][0];
            int gy = cy + startPos[i][1];
            _ghosts.add(new GhostData(gx, gy, i % 2));
        }

        countFoodOnBoard();
    }

    // Counts the total amount of food remaining on the board to check for victory condition.
    private void countFoodOnBoard() {
        _foodCount = 0;
        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                if (_board[x][y] == FOOD_COLOR) _foodCount++;
            }
        }
        System.out.println("Food remaining to win: " + _foodCount);
    }

    // Main game loop function: moves player, checks collisions, moves ghosts, and returns the game state.
    @Override
    public String move(int dir) {
        if (_gameOver) return _victory ? "Victory!" : "Game Over";
        updatePlayer(dir);
        checkCollisions();
        if (_foodCount == 0) {
            _victory = true; _gameOver = true;
            return "Victory!";
        }
        updateGhosts();
        checkCollisions();
        if (_gameOver) return _victory ? "Victory!" : "Game Over";
        return "Score: " + _score;
    }

    // Checks if the player collides with any ghost and handles the interaction (eat ghost or game over).
    private void checkCollisions() {
        if (_gameOver) return;
        int cx = _width / 2; int cy = _height / 2;
        for (GhostData g : _ghosts) {
            if (g.getPos().getX() == _playerPos.getX() && g.getPos().getY() == _playerPos.getY()) {
                if (g.isEatable()) {
                    _score += 60;
                    g.reset(new Index2D(cx, cy));
                } else {
                    _gameOver = true;
                }
            }
        }
    }

    // Updates the player's position based on direction and handles food/power-up collection.
    private void updatePlayer(int dir) {
        if (_gameOver) return;
        int dx = 0, dy = 0;
        if (dir == UP) dy = 1; else if (dir == DOWN) dy = -1;
        else if (dir == LEFT) dx = -1; else if (dir == RIGHT) dx = 1;
        if (dx == 0 && dy == 0) return;

        int nextX = (_playerPos.getX() + dx + _width) % _width;
        int nextY = (_playerPos.getY() + dy + _height) % _height;
        int cx = _width / 2; int cy = _height / 2;
        if (Math.abs(nextX - cx) <= 1 && Math.abs(nextY - cy) <= 1) return;

        int cell = _board[nextX][nextY];
        if (cell != WALL_COLOR) {
            _playerPos = new Index2D(nextX, nextY);
            if (cell == FOOD_COLOR) {
                _score++; _foodCount--; _board[nextX][nextY] = EMPTY_COLOR;
            } else if (cell == POWER_COLOR) {
                _score += 20; _board[nextX][nextY] = EMPTY_COLOR; makeGhostsEdible();
            }
        }
    }

    // Sets all ghosts to 'eatable' state for a fixed duration.
    private void makeGhostsEdible() { for (GhostData g : _ghosts) g.setEatable(100); }

    // Iterates over all ghosts and updates their position based on their AI logic.
    private void updateGhosts() {
        if (_gameOver) return;
        int cx = _width / 2; int cy = _height / 2;
        Pixel2D exitTarget = new Index2D(cx, cy + 2);
        for (GhostData g : _ghosts) {
            g.tick();
            boolean inHouse = Math.abs(g.getPos().getX() - cx) <= 1 && Math.abs(g.getPos().getY() - cy) <= 1;
            if (inHouse) g.moveToTarget(exitTarget, _board, _width, _height, null);
            else g.chaseOrFlee(_playerPos, _board, _width, _height, cx, cy, 2, _ghosts);
        }
    }

    // Returns the current game board grid.
    @Override public int[][] getGame(int type) { return _board; }

    // Returns the current position of the player as a string.
    @Override public String getPos(int type) { return _playerPos.toString() + ",0"; }

    // Returns the status of the game (0 for running, 3 for finished).
    @Override public int getStatus() { return _gameOver ? 3 : 0; }

    // Returns true if the game map is cyclic (wraps around).
    @Override public boolean isCyclic() { return true; }

    // Returns the next key character pressed by the user (defaulting to 'A' if not implemented).
    @Override public Character getKeyChar() { return 'A'; }

    // Returns the string representation of a specific ghost (unused).
    public String getGhost(int i) { return ""; }

    // Placeholder for starting/resuming the game (unused).
    @Override public void play() {}

    // Returns the final game message (Victory or Game Over).
    @Override public String end(int i) { return _victory ? "Victory!" : "Game Over"; }

    // Returns data string, specifically the current score.
    @Override public String getData(int i) { return ""+_score; }

    // Returns an array of ghost objects representing their current state.
    @Override
    public GhostCL[] getGhosts(int type) {
        GhostCL[] res = new GhostCL[_ghosts.size()];
        for(int i=0; i<_ghosts.size(); i++) {
            GhostData g = _ghosts.get(i);
            res[i] = new GhostCL() {
                public int getType() { return 0; }
                public String getPos(int t) { return g.getPos().toString()+",0"; }
                public String getInfo() { return ""; }
                public double remainTimeAsEatable(int t) { return g.getEatableTime(); }
                public int getStatus() { return 1; }
            };
        }
        return res;
    }


    private static class GhostData {
        private Pixel2D _pos;
        private double _eatableTime = 0;
        private int _type;

        // Constructor for GhostData, initializing position and type.
        public GhostData(int x, int y, int type) { _pos = new Index2D(x, y); _type = type; }

        // Returns the current position of the ghost.
        public Pixel2D getPos() { return _pos; }

        // Sets the time remaining for the ghost to be eatable.
        public void setEatable(int t) { _eatableTime = t; }

        // Returns the remaining time the ghost is eatable.
        public double getEatableTime() { return _eatableTime; }

        // Decrements the eatable timer by one tick.
        public void tick() { if (_eatableTime > 0) _eatableTime--; }

        // Resets the ghost to its home position and clears eatable status.
        public void reset(Pixel2D home) { _pos = home; _eatableTime = 0; }

        // Checks if the ghost is currently eatable.
        public boolean isEatable() { return _eatableTime > 0; }

        // Moves the ghost towards a specific target position.
        public void moveToTarget(Pixel2D target, int[][] board, int w, int h, ArrayList<GhostData> others) {
            moveSmart(target, board, w, h, false, -1, -1, -1, others);
        }

        // Decides whether to chase the player or flee based on ghost type and eatable state.
        public void chaseOrFlee(Pixel2D target, int[][] board, int w, int h, int hx, int hy, int hr, ArrayList<GhostData> others) {
            if (_type == 1 && !isEatable()) {
                if (isNeighbor(_pos, target, w, h)) _pos = new Index2D(target.getX(), target.getY());
                else moveRandom(board, w, h, hx, hy, hr, others);
            } else moveSmart(target, board, w, h, isEatable(), hx, hy, hr, others);
        }

        // Moves the ghost to a random valid neighboring cell.
        private void moveRandom(int[][] board, int w, int h, int hx, int hy, int hr, ArrayList<GhostData> others) {
            int[] dxs = {0, 0, 1, -1}; int[] dys = {1, -1, 0, 0};
            for (int i = 0; i < 10; i++) {
                int r = (int)(Math.random() * 4);
                int nx = (_pos.getX() + dxs[r] + w) % w;
                int ny = (_pos.getY() + dys[r] + h) % h;
                if (isValidMove(nx, ny, board, hx, hy, hr, others)) { _pos = new Index2D(nx, ny); return; }
            }
        }

        // Moves the ghost smartly towards (or away from) the target using a heuristic.
        private void moveSmart(Pixel2D target, int[][] board, int w, int h, boolean flee, int hx, int hy, int hr, ArrayList<GhostData> others) {
            if (!flee && isNeighbor(_pos, target, w, h)) { _pos = new Index2D(target.getX(), target.getY()); return; }
            if (!flee && Math.random() < 0.15) { moveRandom(board, w, h, hx, hy, hr, others); return; }
            int cx = _pos.getX(), cy = _pos.getY(); int tx = target.getX(), ty = target.getY();
            int dx = 0, dy = 0;
            if (Math.abs(cx - tx) > Math.abs(cy - ty)) dx = (cx < tx) ? 1 : -1; else dy = (cy < ty) ? 1 : -1;
            if (flee) { dx = -dx; dy = -dy; }
            int nx = (cx + dx + w) % w; int ny = (cy + dy + h) % h;
            if (isValidMove(nx, ny, board, hx, hy, hr, others)) _pos = new Index2D(nx, ny);
            else moveRandom(board, w, h, hx, hy, hr, others);
        }

        // Checks if a move to a coordinate is valid (not a wall or occupied by another ghost).
        private boolean isValidMove(int x, int y, int[][] board, int hx, int hy, int hr, ArrayList<GhostData> others) {
            if (board[x][y] == WALL_COLOR) return false;
            if (hx != -1 && Math.abs(x - hx) <= hr && Math.abs(y - hy) <= hr) return false;
            if (others != null) for (GhostData other : others) if (other != this && other.getPos().getX() == x && other.getPos().getY() == y) return false;
            return true;
        }

        // Checks if two pixels are neighbors on the grid (considering cyclic borders).
        private boolean isNeighbor(Pixel2D p1, Pixel2D p2, int w, int h) {
            int dx = Math.abs(p1.getX() - p2.getX()); int dy = Math.abs(p1.getY() - p2.getY());
            if (dx > 1) dx = w - dx; if (dy > 1) dy = h - dy;
            return (dx + dy <= 1);
        }
    }
}