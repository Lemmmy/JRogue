package jr.utils;

import jr.JRogue;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for profiling function call duration.
 */
public class Profiler {
	public static final Level LEVEL = Level.forName("TIME", 550);
	
	private static final Map<String, Long> startTimes = new HashMap<>();
	
	/**
	 * Starts a timer under the name {@code name}.
	 *
	 * @param name The name of the timer.
	 */
	public static void start(String name) {
		startTimes.put(name, System.nanoTime());
	}
	
	/**
	 * Finishes the timer under the name {@code name}, and logs the duration (in milliseconds) to the
	 * console with the log level {@code PROFILER}. The timer must have already been started with
	 * {@link #start(String)}.
	 *
	 * @param name The name of the timer.
	 * @return The time the timer took (i.e. the time between the {@link #start(String)} and
	 *         {@link #end(String)} calls, in <strong>nanoseconds</strong>.
	 */
	public static long end(String name) {
		long start = startTimes.getOrDefault(name, System.nanoTime());
		long end = System.nanoTime();
		long duration = end - start;
		
		JRogue.getLogger().log(LEVEL, String.format("[%s30]: %,d ms", name, duration / 1_000_000));
		
		return duration;
	}
	
	/**
	 * Runs the {@link Runnable} {@code runnable}, and logs the duration (in milliseconds) to the
	 * console, the same as {@link #end(String)}. Also returns the duration in nanoseconds.
	 *
	 * @param name The name of the timer.
	 * @param runnable The {@link Runnable} to run.
	 * @return The time the {@link Runnable} took to run, in <strong>nanoseconds</strong>.
	 */
	public static long time(String name, Runnable runnable) {
		start(name);
		runnable.run();
		return end(name);
	}
}
