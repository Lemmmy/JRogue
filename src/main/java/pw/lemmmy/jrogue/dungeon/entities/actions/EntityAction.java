package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public abstract class EntityAction {
	private Dungeon dungeon;
	private Entity entity;

	public EntityAction(Dungeon dungeon, Entity entity) {
		this.dungeon = dungeon;
		this.entity = entity;
	}

	public abstract void execute();

	public Dungeon getDungeon() {
		return dungeon;
	}

	public Entity getEntity() {
		return entity;
	}
}
