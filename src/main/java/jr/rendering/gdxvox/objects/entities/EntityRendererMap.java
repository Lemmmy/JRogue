package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.events.EntityAddedEvent;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.events.EntityRemovedEvent;
import jr.dungeon.events.EventHandler;
import jr.rendering.gdxvox.context.SceneContext;
import jr.rendering.gdxvox.objects.AbstractObjectRendererMap;

import java.lang.annotation.Annotation;

public class EntityRendererMap extends AbstractObjectRendererMap<EntityAppearance, Entity, EntityRenderer> {
	public EntityRendererMap(SceneContext scene) {
		super(scene);
	}
	
	@Override
	public void findObjects(Level level) {
		for (Entity entity : level.entityStore.getEntities()) {
			EntityAppearance appearance = entity.getAppearance();
			
			if (!objectRendererMap.containsKey(appearance)) continue;
			objectRendererMap.get(appearance).objectAdded(entity, getScene());
		}
	}
	
	@EventHandler
	private void onEntityAdded(EntityAddedEvent e) {
		EntityAppearance appearance = e.getEntity().getAppearance();
		
		if (!objectRendererMap.containsKey(appearance)) return;
		objectRendererMap.get(appearance).objectAdded(e.getEntity(), getScene());
	}
	
	@EventHandler
	private void onEntityRemoved(EntityRemovedEvent e) {
		EntityAppearance appearance = e.getEntity().getAppearance();
		
		if (!objectRendererMap.containsKey(appearance)) return;
		objectRendererMap.get(appearance).objectRemoved(e.getEntity(), getScene());
	}
	
	@EventHandler
	public void onEntityMoved(EntityMovedEvent e) {
		EntityAppearance appearance = e.getEntity().getAppearance();
		
		if (!objectRendererMap.containsKey(appearance)) return;
		EntityRenderer r = objectRendererMap.get(appearance);
		r.entityMoved(e.getEntity(), e, r.getBatch(), getScene());
	}
	
	@Override
	public Class<? extends EntityAppearance> getObjectKeyClass() {
		return EntityAppearance.class;
	}
	
	@Override
	public Class<? extends Entity> getObjectValueClass() {
		return Entity.class;
	}
	
	@Override
	public Class<? extends Annotation> getListAnnotationClass() {
		return EntityRendererList.class;
	}
}
