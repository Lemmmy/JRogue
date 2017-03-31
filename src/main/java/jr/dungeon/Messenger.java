package jr.dungeon;

import jr.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A messenger is something that can be logged to. Has lots of utility methods for printing common strings with colours.
 */
public interface Messenger {
	List<String> getHistory();
	
	/**
	 * Log a formatted string.
	 *
	 * @param s The string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 *
	 * @see String#format
	 */
	void log(String s, Object... objects);
	
	/**
	 * Logs a random string from the list of strings.
	 *
	 * @param strings List of strings to choose from.
	 */
	default void logRandom(String... strings) {
		log(RandomUtils.randomFrom(strings));
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'The ' automatically added.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void The(String s, Object... objects) {
		log("The " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'The ' automatically added, in the colour red.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void redThe(String s, Object... objects) {
		log("[RED]The " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'The ' automatically added, in the colour orange.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void orangeThe(String s, Object... objects) {
		log("[ORANGE]The " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'The ' automatically added, in the colour yellow.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void yellowThe(String s, Object... objects) {
		log("[YELLOW]The " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'The ' automatically added, in the colour green.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void greenThe(String s, Object... objects) {
		log("[GREEN]The " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'You ' automatically added.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void You(String s, Object... objects) {
		log("You " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'You ' automatically added, in the colour red.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void redYou(String s, Object... objects) {
		log("[RED]You " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'You ' automatically added, in the colour orange.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void orangeYou(String s, Object... objects) {
		log("[ORANGE]You " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'You ' automatically added, in the colour yellow.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void yellowYou(String s, Object... objects) {
		log("[YELLOW]You " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'You ' automatically added, in the colour green.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void greenYou(String s, Object... objects) {
		log("[GREEN]You " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'Your ' automatically added.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void Your(String s, Object... objects) {
		log("Your " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'Your ' automatically added, in the colour red.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void redYour(String s, Object... objects) {
		log("[RED]Your " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'Your ' automatically added, in the colour orange.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void orangeYour(String s, Object... objects) {
		log("[ORANGE]Your " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'Your ' automatically added, in the colour yellow.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void yellowYour(String s, Object... objects) {
		log("[YELLOW]Your " + s, objects);
	}
	
	/**
	 * {@link #log(String, Object...) Logs} a string with the prefix 'Your ' automatically added, in the colour green.
	 *
	 * @param s The rest of the string to log. Can contain {@link String#format} strings.
	 * @param objects The objects to pass to {@link String#format}.
	 */
	default void greenYour(String s, Object... objects) {
		log("[GREEN]Your " + s, objects);
	}
}
