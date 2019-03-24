package jr.dungeon.io;

import jr.dungeon.Dungeon;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * A prompt is a request for user input from a list of single-character options. Long lists of options will be
 * collapsed into ranges, e.g. <code>abcdef</code> collapses into <code>a-f</code>.
 */
public class Prompt {
	/**
	 * The message of the prompt (i.e. the question).
	 */
	@Getter private String message;
	/**
	 * The list of single-character options that are valid responses from the prompt. Can be null. If null, any
	 * response is valid.
	 */
	@Getter private char[] options;
	/**
	 * The {@link PromptCallback} to call when the prompt is responded to, or cancelled.
	 */
	private PromptCallback callback;
	/**
	 * Whether or not the user can quit the prompt with no input, with the escape key.
	 */
	private boolean escapable;
	
	/**
	 * @param message The message of the prompt (i.e. the question).
	 * @param options The list of single-character options that are valid responses from the prompt. Can be null. If
	 *                   null, any response is valid.
	 * @param escapable Whether or not the user can quit the prompt with no input, with the escape key.
	 * @param callback The {@link PromptCallback} to call when the prompt is responded to, or cancelled.
	 */
	public Prompt(String message, char[] options, boolean escapable, PromptCallback callback) {
		this.message = message;
		this.options = options;
		this.callback = callback;
		this.escapable = escapable;
	}
	
	public String getOptionsString() {
		StringBuilder result = new StringBuilder();
		
		char[] chars = options.clone();
		Arrays.sort(chars);
		
		int start, end;
		start = end = chars[0];
		
		for (int i = 1; i < chars.length; i++) {
			if (chars[i] == chars[i - 1] + 1) {
				end = chars[i];
			} else {
				result.append((char) start);
				if (start != end) result.append("-").append((char) end);
				result.append(" ");
				
				start = end = chars[i];
			}
		}
		
		result.append((char) start);
		if (start != end) result.append("-").append((char) end);
		
		return result.toString();
	}
	
	public boolean isEscapable() {
		return escapable;
	}
	
	/**
	 * Escapes the prompt - called when the user presses the escape key in-game. The
	 * {@link PromptCallback#onNoResponse()} is called.
	 */
	public void escape() {
		if (this.escapable) {
			callback.onNoResponse();
		}
	}
	
	/**
	 * Called when the user responds to the prompt in-game. Will call {@link PromptCallback#onResponse(char)} if the
	 * response was valid, {@link PromptCallback#onInvalidResponse(char)} if not.
	 *
	 * @param response The char the user responded with.
	 */
	public void respond(char response) {
		if (options == null) {
			callback.onResponse(response);
		} else {
			response = Character.toLowerCase(response);
			
			if (ArrayUtils.contains(options, response)) {
				callback.onResponse(response);
			} else {
				callback.onInvalidResponse(response);
			}
		}
	}
	
	/**
	 * Callbacks to call when responses (or invalid responses) are provided to the {@link Prompt}.
	 */
	public interface PromptCallback {
		/**
		 * Called when the user escapes the prompt, providing no response.
		 */
		void onNoResponse();
		
		/**
		 * Called when the user provides an invalid response, i.e. a char that was not in {@link Prompt#options}.
		 *
		 * @param response The user's response.
		 */
		void onInvalidResponse(char response);
		
		/**
		 * Called when the user provides a valid response, i.e. a char that is in {@link Prompt#options}.
		 *
		 * @param response The user'rs response.
		 */
		void onResponse(char response);
	}
	
	/**
	 * Simple {@link Prompt} callback that responds with a static message when no response or an invalid response is
	 * received.
	 */
	public static abstract class SimplePromptCallback implements PromptCallback {
		/**
		 * Message to log if the user provides no response ({@link Prompt#escape escapes} the prompt).
		 */
		private final String noResponseMessage;
		/**
		 * Message to log if the user provides an invalid response (a response that is not in {@link Prompt#options}).
		 */
		private final String invalidResponseMessage;
		/**
		 * {@link Messenger} to log to. Typically the {@link Dungeon}.
		 */
		private final Messenger messenger;
		
		/**
		 * @param messenger The {@link Messenger} to log to. Typically the {@link Dungeon}.
		 */
		public SimplePromptCallback(Messenger messenger) {
			this(messenger, "Nevermind.", "Invalid response '[YELLOW]%s[]'.");
		}
		
		/**
		 * @param messenger The {@link Messenger} to log to. Typically the {@link Dungeon}.
		 * @param noResponseMsg The message to log if the user provides no response ({@link Prompt#escape escapes} the
		 *                            prompt).
		 * @param invalidResponseMsg The message to log if the user provides an invalid response (a response that is
		 *                                 not in {@link Prompt#options}).
		 */
		public SimplePromptCallback(Messenger messenger, String noResponseMsg, String invalidResponseMsg) {
			this.messenger = messenger;
			this.noResponseMessage = noResponseMsg;
			this.invalidResponseMessage = invalidResponseMsg;
		}
		
		@Override
		public void onNoResponse() {
			messenger.log(noResponseMessage);
		}
		
		@Override
		public void onInvalidResponse(char response) {
			messenger.log(invalidResponseMessage, response);
		}
		
		@Override
		public abstract void onResponse(char response);
	}
}