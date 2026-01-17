package Ex3P;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Index2D_Test {

    @Test
    void testGettersAndConstructor() {
        Index2D a = new Index2D(5, 6);
        assertEquals(5, a.getX());  // x shoud be 5
        assertEquals(6, a.getY());  // y shoud be 6
    }

    @Test
    void testGettersAndConstructor2() {
        Index2D p = new Index2D(3, 4);
        assertEquals(3, p.getX(), "X should be 3");
        assertEquals(4, p.getY(), "Y should be 4");
    }
    @Test
    void testCopyConstructor() {
        Index2D p1 = new Index2D(5, 10);
        Index2D p2 = new Index2D(p1);

        assertEquals(p1.getX(), p2.getX());
        assertEquals(p1.getY(), p2.getY());
        assertNotSame(p1, p2, "Copy constructor should create a new object");
    }
    // test for toString function
    @Test
    void testToString() {
        Index2D u = new Index2D(7,8);
        assertEquals("7,8",u.toString());

    }
    // test for distance function from - Pythagoras' theorem - the distance from points is sqrt between
    // addind  squared of both of them
    @Test
    void testDistance2D() {
        Index2D p1 = new Index2D(0, 0);
        Index2D p2 = new Index2D(3, 4);

        double dist = p1.distance2D(p2);
        assertEquals(5.0, dist, 0.001);

    }
    // another test for distance , distance of point to himself equals zero
    @Test
    void testDistance2DBetweenhimselfe() {
        Index2D p2 = new Index2D(3, 4);

        double dist = p2.distance2D(p2);
        assertEquals(0.0, dist, 0.001);

    }
    // test for equals function if points are equals
    @Test
    void testEquals() {
        Index2D p1 = new Index2D(5, 5);
        Index2D p2 = new Index2D(5, 5);
        Index2D p3 = new Index2D(2, 3);
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
        assertFalse(p1.equals(p3));


    }


}