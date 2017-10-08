package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.events.EventHandler;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.AbstractObjectRendererMap;

import java.lang.annotation.Annotation;

public class EntityRendererMap extends AbstractObjectRendererMap {
	@Override
	public void findObjects(Level level) {
		for (Entity entity : level.entityStore.getEntities()) {
			EntityAppearance appearance = entity.getAppearance();
			
			if (!objectRendererMap.containsKey(appearance)) continue;
			((EntityRenderer) objectRendererMap.get(appearance)).objectAdded(entity);
		}
	}
	
	@EventHandler
	private void onEntityAdded(EntityAddedEvent e) {
		EntityAppearance appearance = e.getEntity().getAppearance();
		
		if (!objectRendererMap.containsKey(appearance)) return;
		((EntityRenderer) objectRendererMap.get(appearance)).objectAdded(e.getEntity());
	}
	
	@EventHandler
	private void onEntityRemoved(EntityRemovedEvent e) {
		EntityAppearance appearance = e.getEntity().getAppearance();
		
		if (!objectRendererMap.containsKey(appearance)) return;
		((EntityRenderer) objectRendererMap.get(appearance)).objectRemoved(e.getEntity());
	}
	
	@Override
	public Class getObjectKeyClass() {
		return EntityAppearance.class;
	}
	
	@Override
	public Class getObjectValueClass() {
		return Entity.class;
	}
	
	@Override
	public Class<? extends Annotation> getListAnnotationClass() {
		return EntityRendererList.class;
	}
}
