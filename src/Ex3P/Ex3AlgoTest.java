package Ex3P;

import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;

import exe.ex3.game.PacmanGame;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Ex3AlgoTest {

    private Ex3Algo _algo;
    private Map _map;
    private int[][] _board;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;


    private int _blue, _pink, _green;

    @BeforeEach
    void setUp() throws Exception {
        _algo = new Ex3Algo();


        _board = new int[WIDTH][HEIGHT];
        for (int[] row : _board) {
            Arrays.fill(row, Game.getIntColor(Color.WHITE, 0));
        }

        _map = new Map(_board);
        _map.setCyclic(true);

        _blue = Game.getIntColor(Color.BLUE, 0);
        _pink = Game.getIntColor(Color.PINK, 0);
        _green = Game.getIntColor(Color.GREEN, 0);

        setPrivateField(_algo, "_map", _map);
        setPrivateField(_algo, "BLUE", _blue);
        setPrivateField(_algo, "PINK", _pink);
        setPrivateField(_algo, "GREEN", _green);
    }

    @Test
    void testParsePos() throws Exception {
        Pixel2D p = (Pixel2D) invokePrivateMethod(_algo, "parsePos", new Class[]{String.class}, "5,10");
        assertNotNull(p);
        assertEquals(5, p.getX());
        assertEquals(10, p.getY());
    }


    @Test
    void testCheksneighbor_Simple() {
        Pixel2D center = new Index2D(10, 10);
        Pixel2D up = _algo.cheksneighbor(center, Game.UP, _map);
        assertEquals(10, up.getX());
        assertEquals(11, up.getY());
    }

    @Test
    void testCheksneighbor_Cyclic() {
        Pixel2D edge = new Index2D(WIDTH - 1, 10);
        Pixel2D wrapped = _algo.cheksneighbor(edge, Game.RIGHT, _map);
        assertEquals(0, wrapped.getX()); // צריך לחזור להתחלה (0)
    }

    @Test
    void testIsLegaltomove() {
        // הצבת קיר בלוח
        _board[5][5] = _blue;

        Pixel2D wallPos = new Index2D(5, 5);
        Pixel2D freePos = new Index2D(6, 6);
        Pixel2D outOfBounds = new Index2D(-1, 0);

        assertFalse(_algo.isLegaltomove(wallPos, _board), "Should return false for wall");
        assertTrue(_algo.isLegaltomove(freePos, _board), "Should return true for empty space");
        assertFalse(_algo.isLegaltomove(outOfBounds, _board), "Should return false for out of bounds");
    }



    @Test
    void testWithinGhostHouse_WithWall() {

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        _board[centerX][centerY] = Game.getIntColor(Color.BLUE, 0);
        Pixel2D center = new Index2D(centerX, centerY);

        assertFalse(_algo.WithinGhostHouse(center, _board),
                "Should return false if there is a wall, even inside the house boundaries");
    }

    @Test
    void testClosestPINK() {
        _board[2][2] = _pink;

        Pixel2D start = new Index2D(2, 5);
        Map2D distMap = _map.allDistance(start, _blue);

        Pixel2D result = _algo.closestPINK(_board, distMap, _pink);

        assertNotNull(result);
        assertEquals(2, result.getX());
        assertEquals(2, result.getY());
    }

    @Test
    void testCalculatSafeSpace() {
        Pixel2D start = new Index2D(5, 5);
        double[][] safeDangerMap = new double[WIDTH][HEIGHT];
        for (double[] r : safeDangerMap) Arrays.fill(r, 99.0);

        int limit = 5;
        int count = _algo.calculatSafeSpace(start, _map, _board, safeDangerMap, limit);

        assertEquals(limit, count);
    }





    // ==========================================
    //        פונקציות עזר ל-Reflection
    // ==========================================

    private void setPrivateField(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    private Object invokePrivateMethod(Object instance, String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = instance.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(instance, args);
    }
}