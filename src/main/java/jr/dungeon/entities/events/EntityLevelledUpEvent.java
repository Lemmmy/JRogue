package jr.dungeon.entities.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.events.DungeonEvent;

public class EntityLevelledUpEvent extends DungeonEvent {
	private Entity entity;
	private int newExperienceLevel;
	
	public EntityLevelledUpEvent(Entity entity, int newExperienceLevel) {
		this.entity = entity;
		this.newExperienceLevel = newExperienceLevel;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public int getNewExperienceLevel() {
		return newExperienceLevel;
	}
	
	@Override
	public boolean isSelf(Object other) {
		return other.equals(entity);
	}
}
