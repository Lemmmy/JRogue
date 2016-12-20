package pw.lemmmy.jrogue.utils;

import com.github.alexeyr.pcg.Pcg32;

import java.util.NavigableMap;
import java.util.TreeMap;

public class WeightedCollection<E> {
	private static final Pcg32 rand = new Pcg32();

	private NavigableMap<Integer, E> map = new TreeMap<>();
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
}
