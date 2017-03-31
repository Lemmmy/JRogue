package jr.dungeon;

import java.util.LinkedList;
import java.util.List;

/**
 * Blank messenger that does nothing when logging.
 */
public class BlankMessenger implements Messenger {
	@Override
	public List<String> getHistory() {
		return new LinkedList<>();
	}
	
	@Override
	public void log(String s, Object... objects) {
		
	}
}
