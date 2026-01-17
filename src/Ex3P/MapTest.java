package Ex3P;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    private Map _map;
    private final int WIDTH = 10;
    private final int HEIGHT = 10;
    private final int DEFAULT_VAL = 0;

    @BeforeEach
    void setUp() {
        _map = new Map(WIDTH, HEIGHT, DEFAULT_VAL);
        _map.setCyclic(false);
    }



    @Test
    void testConstructorWithSize() {
        Map m = new Map(5); // יוצר 5x5
        assertEquals(5, m.getWidth());
        assertEquals(5, m.getHeight());
        assertEquals(0, m.getPixel(0, 0));
    }

    @Test
    void testConstructorWithArray() {
        int[][] data = {
                {1, 2},
                {3, 4},
                {5, 6}
        };
        Map m = new Map(data);
        assertEquals(3, m.getWidth());
        assertEquals(2, m.getHeight());
        assertEquals(1, m.getPixel(0, 0));
        assertEquals(6, m.getPixel(2, 1));
    }



    @Test
    void testInit() {
        _map.init(20, 15, 7);
        assertEquals(20, _map.getWidth());
        assertEquals(15, _map.getHeight());
        assertEquals(7, _map.getPixel(10, 10));
    }

    @Test
    void testInitWithArray() {
        int[][] arr = {{9, 8}, {7, 6}};
        _map.init(arr);
        assertEquals(2, _map.getWidth());
        assertEquals(2, _map.getHeight());
        assertEquals(9, _map.getPixel(0, 0));
    }

    @Test
    void testGetMap() {
        _map.init(3, 3, 1);
        int[][] copy = _map.getMap();
        assertNotNull(copy);
        assertEquals(3, copy.length);
        assertEquals(3, copy[0].length);


        copy[0][0] = 100;
        assertEquals(1, _map.getPixel(0, 0), "Change in copy should not affect original map");
    }



    @Test
    void testSetAndGetPixel() {
        _map.setPixel(1, 1, 5);
        assertEquals(5, _map.getPixel(1, 1));

        Pixel2D p = new Index2D(2, 2);
        _map.setPixel(p, 10);
        assertEquals(10, _map.getPixel(p));
        assertEquals(10, _map.getPixel(2, 2));
    }




    @Test
    void testIsInside() {
        assertTrue(_map.isInside(new Index2D(0, 0)));
        assertTrue(_map.isInside(new Index2D(WIDTH - 1, HEIGHT - 1)));
        assertFalse(_map.isInside(new Index2D(WIDTH, 5)));
        assertFalse(_map.isInside(new Index2D(-1, 0)));
    }



    @Test
    void testGetNeighborsMiddle() {
        Pixel2D p = new Index2D(5, 5);
        Queue<Pixel2D> neighbors = _map.getNeighbers(p, false);

        assertEquals(4, neighbors.size());
    }

    @Test
    void testGetNeighborsCornerNonCyclic() {
        Pixel2D p = new Index2D(0, 0);
        Queue<Pixel2D> neighbors = _map.getNeighbers(p, false);
        assertEquals(2, neighbors.size());
    }

    @Test
    void testGetNeighborsCornerCyclic() {
        Pixel2D p = new Index2D(0, 0);
        Queue<Pixel2D> neighbors = _map.getNeighbers(p, true);

        assertEquals(4, neighbors.size());

        boolean foundWrapX = false;
        boolean foundWrapY = false;
        for (Pixel2D n : neighbors) {
            if (n.getX() == WIDTH - 1) foundWrapX = true;
            if (n.getY() == HEIGHT - 1) foundWrapY = true;
        }
        assertTrue(foundWrapX && foundWrapY, "Should include wrapped neighbors");
    }


    @Test
    void testFillSimple() {
        _map.init(5, 5, 0);

        int count = _map.fill(new Index2D(2, 2), 1);

        assertEquals(25, count); // כל ה-25 תאים נצבעו
        assertEquals(1, _map.getPixel(0, 0));
        assertEquals(1, _map.getPixel(4, 4));
    }

    @Test
    void testFillWithBoundary() {
        _map.init(5, 5, 0);

        for(int y=0; y<5; y++) _map.setPixel(2, y, 9);

        int count = _map.fill(new Index2D(0, 0), 5);

        assertEquals(10, count);
        assertEquals(5, _map.getPixel(0, 0));
        assertEquals(9, _map.getPixel(2, 2));
        assertEquals(0, _map.getPixel(3, 0));
    }


    @Test
    void testAllDistanceSimple() {
        _map.init(3, 3, 0);
        Pixel2D start = new Index2D(0, 0);

        Map2D distMap = _map.allDistance(start, 1);

        assertEquals(0, distMap.getPixel(0, 0));
        assertEquals(1, distMap.getPixel(0, 1));
        assertEquals(2, distMap.getPixel(1, 1));
        assertEquals(4, distMap.getPixel(2, 2));
    }

    @Test
    void testAllDistanceCyclic() {
        _map.init(10, 10, 0);
        _map.setCyclic(true);
        Pixel2D start = new Index2D(0, 0);

        Map2D distMap = _map.allDistance(start, 1);

        assertEquals(1, distMap.getPixel(9, 0));
    }

    @Test
    void testAllDistanceBlocked() {
        _map.init(3, 3, 0);
        _map.setPixel(1, 2, 1);
        _map.setPixel(2, 1, 1);

        Pixel2D start = new Index2D(0, 0);
        Map2D distMap = _map.allDistance(start, 1);

        assertEquals(-1, distMap.getPixel(2, 2));
    }


    @Test
    void testShortestPathSimple() {
        _map.init(5, 5, 0);
        Pixel2D start = new Index2D(0, 0);
        Pixel2D end = new Index2D(4, 0);

        Pixel2D[] path = _map.shortestPath(start, end, 1);

        assertNotNull(path);
        assertEquals(5, path.length);
        assertEquals(start, path[0]);
        assertEquals(end, path[4]);
    }

    @Test
    void testShortestPathCyclic() {
        _map.init(10, 10, 0);
        _map.setCyclic(true);

        Pixel2D start = new Index2D(0, 5);
        Pixel2D end = new Index2D(9, 5);

        Pixel2D[] path = _map.shortestPath(start, end, 1);

        assertNotNull(path);
        assertEquals(2, path.length);
        assertEquals(start, path[0]);
        assertEquals(end, path[1]);
    }

    @Test
    void testShortestPathNoPath() {
        _map.init(3, 3, 0);
        _map.setPixel(1, 0, 1);
        _map.setPixel(1, 1, 1);
        _map.setPixel(1, 2, 1);

        Pixel2D start = new Index2D(0, 0);
        Pixel2D end = new Index2D(2, 0);

        Pixel2D[] path = _map.shortestPath(start, end, 1);

        assertNull(path, "Path should be null if unreachable");
    }
}

