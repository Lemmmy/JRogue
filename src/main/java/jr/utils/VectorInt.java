package jr.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;

@JsonAdapter(VectorInt.VectorIntTypeAdapter.class)
public final class VectorInt {
    @Expose public final int x, y;
    
    private static final HashMap<Long, VectorInt> vectorCache = new HashMap<>();
    public static VectorInt ZERO = VectorInt.get(0, 0);
    
    public static VectorInt get(int x, int y) {
        long hash = hash(x, y);
        
        if (vectorCache.containsKey(hash)) {
            return vectorCache.get(hash);
        } else {
            VectorInt newVector = new VectorInt(x, y);
            vectorCache.put(hash, newVector);
            return newVector;
        }
    }
    
    /**
     * Gets the vector between two {@link Point Points}. The vector calculated is {@code b} - {@code a}.
     *
     * @param a The first {@link Point}.
     * @param b The second {@link Point}.
     */
    public static VectorInt between(Point a, Point b) {
        return get(b.x - a.x, b.y - a.y);
    }

    private VectorInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public VectorInt add(int x, int y) {
        return VectorInt.get(this.x + x, this.y + y);
    }

    public VectorInt add(VectorInt v) {
        return add(v.x, v.y);
    }

    public VectorInt sub(int x, int y) {
        return VectorInt.get(this.x - x, this.y - y);
    }

    public VectorInt sub(VectorInt v) {
        return sub(v.x, v.y);
    }

    public VectorInt mul(int x, int y) {
        return VectorInt.get(this.x * x, this.y * y);
    }

    public VectorInt mul(VectorInt v) {
        return mul(v.x, v.y);
    }

    public VectorInt div(int x, int y) {
        return VectorInt.get(this.x / x, this.y / y);
    }

    public VectorInt div(VectorInt v) {
        return div(v.x, v.y);
    }
    
    public VectorInt abs() {
        return VectorInt.get(Math.abs(x), Math.abs(y));
    }

    public int dot(VectorInt v) {
        return x * v.x + y * v.y;
    }

    public int lengthSq() {
        return x * x + y * y;
    }

    public int length() {
        return (int) Math.sqrt(lengthSq());
    }

    public VectorInt normalised() {
        int len = length();
        return div(len, len);
    }

    public Point toPoint() {
        return Point.get(x, y);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        
        VectorInt vector = (VectorInt) o;
        return x == vector.x && y == vector.y;
    }
    
    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }
    
    @Override
    public int hashCode() {
        return (int) hash(x, y);
    }
    
    public static long hash(int x, int y) {
        return ((long) x << 32) + (long) y;
    }
    
    @Override
    public String toString() {
        return String.format("%,d, %,d", x, y);
    }
    
    public class VectorIntTypeAdapter extends TypeAdapter<VectorInt> {
        @Override
        public void write(JsonWriter out, VectorInt value) throws IOException {
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
        public VectorInt read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return VectorInt.ZERO;
            }
            
            in.beginArray();
            int x = in.nextInt();
            int y = in.nextInt();
            in.endArray();
            
            return VectorInt.get(x, y);
        }
    }
}
