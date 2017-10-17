package jr.rendering.gdxvox.context;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.EventPriority;
import jr.dungeon.events.LevelChangeEvent;

public abstract class Context implements EventListener {
	private Dungeon dungeon;
	private Level level;
	
	public Context(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.level = dungeon.getLevel();
		this.dungeon.eventSystem.addListener(this);
	}
	
	public void update() {}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	protected void onLevelChange(LevelChangeEvent levelChangeEvent) {
		this.level = levelChangeEvent.getLevel();
	}
	
	public void resize(int width, int height) {}
}
