package pw.lemmmy.jrogue.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplacer {
	public interface Callback {
		public String replace(Matcher match);
	}

	public static String replace(String input, Pattern regex, Callback callback) {
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(input);

		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(resultString, callback.replace(regexMatcher));
		}

		regexMatcher.appendTail(resultString);

		return resultString.toString();
	}
}
