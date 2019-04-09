package jr.utils;

public final class Distance {
    // ints
    public static int i(int ax, int ay, int bx, int by) {
        return (int) Math.sqrt(sqf(ax, ay, bx, by));
    }
    
    public static int i(Point a, Point b) {
        return i(a.x, a.y, b.x, b.y);
    }
    
    // floats
    public static float f(float ax, float ay, float bx, float by) {
        return (float) Math.sqrt(sqf(ax, ay, bx, by));
    }
    
    public static float f(Point a, Point b) {
        return f((float) a.x, (float) a.y, (float) b.x, (float) b.y);
    }
    
    public static float sqf(float ax, float ay, float bx, float by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
    }
    
    public static float sqf(Point a, Point b) {
        return sqf((float) a.x, (float) a.y, (float) b.x, (float) b.y);
    }
    
    // doubles
    public static double d(double ax, double ay, double bx, double by) {
        return Math.sqrt(sqd(ax, ay, bx, by));
    }
    
    public static double sqd(double ax, double ay, double bx, double by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
    }
    
    public static int chebyshev(int ax, int ay, int bx, int by) {
        int dx = Math.abs(ax - bx);
        int dy = Math.abs(ay - by);
        
        return Math.max(dy, dx);
    }
    
    public static int chebyshev(Point a, Point b) {
        return chebyshev(a.x, a.y, b.x, b.y);
    }
    
    public static float octile(int ax, int ay, int bx, int by, float d, float d2) {
        int dx = Math.abs(ax - bx);
        int dy = Math.abs(ay - by);
        
        return d * (dx + dy) + (d2 - 2 * d) * Math.min(dx, dy);
    }
    
    public static float octile(Point a, Point b, float d, float d2) {
        return octile(a.x, a.y, b.x, b.y, d, d2);
    }
}
