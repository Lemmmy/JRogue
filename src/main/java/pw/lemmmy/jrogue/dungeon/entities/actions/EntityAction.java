package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.entities.Entity;

public abstract class EntityAction {
	protected Entity entity;

	public EntityAction(Entity entity) {
		this.entity = entity;
	}

	public abstract int getSpeed();

	public abstract void execute();
}
