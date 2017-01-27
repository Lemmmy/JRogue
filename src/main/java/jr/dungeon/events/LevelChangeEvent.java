package jr.dungeon.events;

import jr.dungeon.Level;

public class LevelChangeEvent extends DungeonEvent {
	private Level level;
	
	public LevelChangeEvent(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
}
