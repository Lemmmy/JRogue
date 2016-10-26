package pw.lemmmy.jrogue.dungeon;

import org.apache.commons.lang3.ArrayUtils;

public class Prompt {
	private String message;
	private char[] options;
	private PromptCallback callback;
	private boolean escapable;

	public Prompt(String message, char[] options, PromptCallback callback, boolean escapable) {
		this.message = message;
		this.options = options;
		this.callback = callback;
		this.escapable = escapable;
	}

	public String getMessage() {
		return message;
	}

	public char[] getOptions() {
		return options;
	}

	public boolean isEscapable() {
		return escapable;
	}

	public void escape() {
		if (this.escapable) {
			callback.onNoResponse();
		}
	}

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

	public interface PromptCallback {
		void onNoResponse();
		void onInvalidResponse(char response);
		void onResponse(char response);
	}
}