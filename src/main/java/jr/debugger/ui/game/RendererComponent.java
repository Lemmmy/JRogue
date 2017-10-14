package jr.debugger.ui.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.LevelChangeEvent;
import lombok.Getter;

@Getter
public abstract class RendererComponent implements EventListener {
	private Dungeon dungeon;
	private Level level;
	
	public RendererComponent(Dungeon dungeon) {
		this.dungeon = dungeon;
		this.level = dungeon.getLevel();
		
		dungeon.eventSystem.addListener(this);
	}
	
	public abstract void draw(SpriteBatch batch);
	
	@EventHandler
	private void onLevelChange(LevelChangeEvent e) {
		level = dungeon.getLevel();
	}
}
