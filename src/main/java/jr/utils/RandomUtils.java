package jr.utils;

import com.github.alexeyr.pcg.Pcg32;
import org.apache.commons.lang3.Range;

import java.util.List;
import java.util.Random;

public class RandomUtils {
    private static final Pcg32 rand = new Pcg32();
    private static final Random jrand = new Random();
    
    @SafeVarargs
    public static <T> T randomFrom(T... items) {
        return items[rand.nextInt(items.length)];
    }
    
    public static <T> T randomFrom(List<T> items) {
        if (items.size() == 0) return null;
        return items.get(rand.nextInt(items.size()));
    }
    
    @SafeVarargs
    public static <T> T jrandomFrom(T... items) {
        return items[jrand.nextInt(items.length)];
    }
    
    public static <T> T jrandomFrom(List<T> items) {
        return items.get(jrand.nextInt(items.size()));
    }
    
    public static int random(int i) {
        return rand.nextInt(i);
    }
    
    /**
     * Quick utility method for generating a random number within a range. Uses the {@link #rand} instance
     * (<a href="http://www.pcg-random.org/">PCG32</a> algorithm).
     *
     * @param min The minimum bound for the random number (inclusive).
     * @param max The maximum bound for the random number (exclusive).
     *
     * @return A (hopefully) random number within the min/max bounds.
     */
    public static int random(int min, int max) {
        return rand.nextInt(max - min) + min;
    }
    
    public static int random(Range<Integer> range) {
        return rand.nextInt(range.getMaximum() - range.getMinimum()) + range.getMinimum();
    }
    
    public static int jrandom(Range<Integer> range) {
        return jrand.nextInt(range.getMaximum() - range.getMinimum()) + range.getMinimum();
    }
    
    public static float randomFloat() {
        return rand.nextFloat();
    }
    
    public static float randomFloat(float f) {
        return rand.nextFloat(f);
    }
    
    public static double randomDouble(double min, double max) {
        return rand.nextDouble(max - min) + min;
    }
    
    public static boolean rollD2() {
        return jrand.nextBoolean();
    }
    
    public static int roll(int x) {
        return roll(1, x);
    }
    
    public static int roll(int a, int x) {
        return roll(a, x, 0);
    }
    
    public static int roll(int a, int x, int b) {
        int o = b;
        
        for (int i = 0; i < a; i++) {
            o += rand.nextInt(x) + 1;
        }
        
        return o;
    }
    
    public static int jroll(int x) {
        return roll(1, x);
    }
    
    public static int jroll(int a, int x) {
        return jroll(a, x, 0);
    }
    
    public static int jroll(int a, int x, int b) {
        int o = b;
        
        for (int i = 0; i < a; i++) {
            o += jrand.nextInt(x) + 1;
        }
        
        return o;
    }
}
