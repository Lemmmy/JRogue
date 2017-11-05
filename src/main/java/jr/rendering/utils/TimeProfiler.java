package jr.rendering.utils;

import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeProfiler {
	public static final int TIME_HISTORY_COUNT = 200;
	
	@Getter private static Map<String, Long> times = new HashMap<>();
	private static Map<String, Long> startTimes = new HashMap<>();
	@Getter private static Map<String, List<Long>> timeHistory = new HashMap<>();
	
	public static void reset() {
		times.keySet().forEach(key -> {
			if (!timeHistory.containsKey(key)) timeHistory.put(key, new ArrayList<>());
			
			val l = timeHistory.get(key);
			if (l.size() > TIME_HISTORY_COUNT) l.remove(0);
			l.add(times.get(key));
		});
		
		times.keySet().forEach(key -> times.put(key, 0L));
		startTimes.keySet().forEach(key -> startTimes.put(key, 0L));
	}
	
	public static void begin(String key) {
		startTimes.put(key, System.nanoTime());
	}
	
	public static void end(String key) {
		long startTime = startTimes.get(key);
		long endTime = System.nanoTime();
		long time = endTime - startTime;
		
		if (times.containsKey(key)) {
			times.put(key, times.get(key) + time);
		} else {
			times.put(key, time);
		}
		
		startTimes.put(key, 0L);
	}
	
	public static List<Long> getTimeHistory(String key) {
		return timeHistory.get(key);
	}
}
