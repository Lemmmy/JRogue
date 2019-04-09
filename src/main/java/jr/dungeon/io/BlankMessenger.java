package jr.dungeon.io;

import java.util.LinkedList;
import java.util.List;

/**
 * Blank messenger that does nothing when logging.
 */
public class BlankMessenger implements Messenger {
    @Override
    public List<String> getLogHistory() {
        return new LinkedList<>();
    }
    
    @Override
    public void log(String s, Object... objects) {
    
    }
}
