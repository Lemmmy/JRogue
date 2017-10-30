package jr.rendering.utils;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TimeProfiler {
	@Getter private static Map<String, Long> times = new HashMap<>();
	private static Map<String, Long> startTimes = new HashMap<>();
	
	public static void reset() {
		times.keySet().forEach(key -> times.put(key, 0L));
		startTimes.keySet().forEach(key -> startTimes.put(key, 0L));
	}
	
	public static void begin(String key) {
		startTimes.put(key, System.nanoTime());
	}
	
	public static void end(String key) {
		long startTime = startTimes.get(key);
		long endTime = System.nanoTime();
		
		if (times.containsKey(key)) {
			times.put(key, times.get(key) + (endTime - startTime));
		} else {
			times.put(key, endTime - startTime);
		}
		
		startTimes.put(key, 0L);
	}
}
