package jr.debugger.ui.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jr.dungeon.Dungeon;
import jr.dungeon.EntityStore;
import jr.dungeon.entities.Entity;
import jr.rendering.entities.EntityMap;

import java.util.Comparator;

public class EntityComponent extends RendererComponent {
	public EntityComponent(Dungeon dungeon) {
		super(dungeon);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		drawEntities(batch);
	}
	
	private void drawEntities(SpriteBatch batch) {
		Dungeon dungeon = getDungeon();
		EntityStore entityStore = getLevel().entityStore;
		
		entityStore.getEntities().stream()
			.sorted(Comparator.comparingInt(Entity::getDepth))
			.forEach(e -> {
				EntityMap em = EntityMap.valueOf(e.getAppearance().name());
				
				if (em.getRenderer() != null) {
					em.getRenderer().draw(batch, dungeon, e, false);
				}
			});
	}
}
