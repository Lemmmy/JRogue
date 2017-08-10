package jr.dungeon.io;

import jr.utils.Utils;
import jr.utils.VectorInt;

public abstract class DirectionPromptCallback extends Prompt.SimplePromptCallback {
	public DirectionPromptCallback(Messenger messenger) {
		super(messenger);
	}
	
	@Override
	public void onResponse(char response) {
		if (!Utils.MOVEMENT_CHARS.containsKey(response)) {
			getMessenger().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
			return;
		}
		
		onDirectionResponse(Utils.MOVEMENT_CHARS.get(response));
	}
	
	public abstract void onDirectionResponse(VectorInt direction);
}
