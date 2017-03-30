package jr.dungeon;

import java.util.ArrayList;

/**
 * Blank messenger that does nothing when logging.
 */
public class BlankMessenger implements Messenger {
	@Override
	public ArrayList<String> getHistory() {
		return new ArrayList<>();
	}
	
	@Override
	public void log(String s, Object... objects) {
		
	}
}
