package pw.lemmmy.jrogue.dungeon;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class Prompt {
	private String message;
	private char[] options;
	private PromptCallback callback;
	private boolean escapable;
	
	public Prompt(String message, char[] options, boolean escapable, PromptCallback callback) {
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
	
	public String getOptionsString() {
		String result = "";
		
		char[] chars = options.clone();
		Arrays.sort(chars);
		
		int start, end;
		start = end = chars[0];
		
		for (int i = 1; i < chars.length; i++) {
			if (chars[i] == (chars[i - 1] + 1)) {
				end = chars[i];
			} else {
				if (start == end) {
					result += (char) start + " ";
				} else {
					result += (char) start + "-" + (char) end + " ";
				}
				
				start = end = chars[i];
			}
		}
		
		if (start == end) {
			result += (char) start;
		} else {
			result += (char) start + "-" + (char) end;
		}
		
		return result;
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

	public abstract static class SimplePromptCallback implements PromptCallback {
		private final String noResponseMessage, invalidResponseMessage;
		private final Messenger messenger;

		public SimplePromptCallback(Messenger msg) {
			this(msg, "Nevermind.", "Invalid response '[YELLOW]%s[]'.");
		}

		public SimplePromptCallback(Messenger msg, String noResponseMsg, String invalidResponseMsg) {
			this.messenger = msg;
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