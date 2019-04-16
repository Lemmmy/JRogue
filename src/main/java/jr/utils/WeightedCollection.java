package jr.utils;

import com.github.alexeyr.pcg.Pcg32;
import lombok.Getter;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class WeightedCollection<E> {
    private static final Pcg32 rand = new Pcg32();
    
    @Getter private NavigableMap<Integer, E> map = new TreeMap<>();
    private int total;
    
    public void add(int weight, E object) {
        if (weight <= 0) { return; }
        total += weight;
        map.put(total, object);
    }
    
    public E next() {
        int value = rand.nextInt(total) + 1;
        return map.ceilingEntry(value).getValue();
    }
    
    /**
     * Allows you to pass your own seeded random.
     *
     * @param random Custom Random object.
     * @return A random item from the collection.
     */
    public E next(Random random) {
        int value = random.nextInt(total) + 1;
        return map.ceilingEntry(value).getValue();
    }
    
    public void clear() {
        total = 0;
        map.clear();
    }
}
