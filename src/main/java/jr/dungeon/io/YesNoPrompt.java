package jr.dungeon.io;

public class YesNoPrompt extends Prompt {
	/**
	 * @param message   The message of the prompt (i.e. the question).
	 * @param escapable Whether or not the user can quit the prompt with no input, with the escape key.
	 *                  If true, escape will be considered a 'no'.
	 * @param callback  The {@link YesNoPromptCallback} to call when the prompt is responded to.
	 */
	public YesNoPrompt(String message, boolean escapable, YesNoPromptCallback callback) {
		super(message, new char[]{'y', 'n'}, escapable, new PromptCallback() {
			@Override
			public void onNoResponse() {
				callback.onResponse(false);
			}
			
			@Override
			public void onInvalidResponse(char response) {
				callback.onResponse(false);
			}
			
			@Override
			public void onResponse(char response) {
				callback.onResponse(response == 'y');
			}
		});
	}
	
	@FunctionalInterface
	public interface YesNoPromptCallback {
		void onResponse(boolean yes);
	}
}
