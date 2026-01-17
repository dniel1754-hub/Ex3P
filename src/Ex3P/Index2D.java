package Ex3P;

//package assignments.Ex3;
public class Index2D implements Pixel2D {
    private int _x;
    private int _y;

    public Index2D() {
        this(0, 0);
    }

    public Index2D(int x, int y) {
        this._x = x;
        this._y = y;
    }

    public Index2D(Pixel2D t) {
        this(t.getX(), t.getY());
    }

    public int getX() {
        return this._x;
    }

    public int getY() {
        return this._y;
    }

    public double distance2D(Pixel2D t) {
        double ans = (double)0.0F;
        if (t == null) {
            return Double.MAX_VALUE;
        } else {
            double dx = (double)(this._x - t.getX());
            double dy = (double)(this._y - t.getY());
            ans = Math.sqrt(dx * dx + dy * dy);
            return ans;
        }
    }

    public String toString() {
        int var10000 = this.getX();
        return var10000 + "," + this.getY();
    }


    public boolean equals(Object t) {
        boolean ans = false;
        if (t instanceof Pixel2D p) {
            ans = this.distance2D(p) == (double)0.0F;
        }

        return ans;
    }
}
