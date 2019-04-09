package jr.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import jr.dungeon.Level;

import java.io.IOException;
import java.util.HashMap;

@JsonAdapter(Point.PointTypeAdapter.class)
public final class Point {
    @Expose public final int x, y;
    
    private static final HashMap<Long, Point> pointCache = new HashMap<>();
    public static Point ZERO = Point.get(0, 0);
    
    public static Point get(int x, int y) {
        long hash = hash(x, y);
        
        if (pointCache.containsKey(hash)) {
            return pointCache.get(hash);
        } else {
            Point newPoint = new Point(x, y);
            pointCache.put(hash, newPoint);
            return newPoint;
        }
    }

    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Point add(int x, int y) {
        return get(this.x + x, this.y + y);
    }
    
    public Point add(VectorInt vector) {
        return get(this.x + vector.x, this.y + vector.y);
    }
    
    public Point setX(int x) {
        return get(x, this.y);
    }
    
    public Point setY(int y) {
        return get(this.x, y);
    }
    
    /**
     * Gets the vector between two {@link Point Points}. The vector calculated is {@code this} - {@code point}, clamped
     * to be between -1 and 1.
     *
     * @param point The other {@link Point}.
     * @return The clamped vector, between -1 and 1.
     */
    public VectorInt clampedDelta(Point point) {
        return VectorInt.get(
            Math.max(-1, Math.min(this.x - point.x, 1)),
            Math.max(-1, Math.min(this.y - point.y, 1))
        );
    }
    
    public boolean within(int minX, int minY, int maxX, int maxY) {
        return x >= minX && y >= minY && x < maxX && y < maxY;
    }
    
    public boolean insideLevel(Level level) {
        return within(0, 0, level.getWidth(), level.getHeight());
    }
    
    /**
     * Gets this point's 1-dimensional index within a {@link Level}.
     *
     * @param level The {@link Level}.
     * @return The 1D index of this point in the {@link Level}.
     */
    public int getIndex(Level level) {
        return y * level.getWidth() + x;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }
    
    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
    
    @Override
    public int hashCode() {
        return y * 31 + x;
    }
    
    public static long hash(int x, int y) {
        return ((long) x << 32) + (long) y;
    }
    
    @Override
    public String toString() {
        return String.format("%,d, %,d", x, y);
    }
    
    public class PointTypeAdapter extends TypeAdapter<Point> {
        @Override
        public void write(JsonWriter out, Point value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            
            out.beginArray();
            out.value(value.x);
            out.value(value.y);
            out.endArray();
        }
        
        @Override
        public Point read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return Point.ZERO;
            }
            
            in.beginArray();
            int x = in.nextInt();
            int y = in.nextInt();
            in.endArray();
            
            return Point.get(x, y);
        }
    }
}
