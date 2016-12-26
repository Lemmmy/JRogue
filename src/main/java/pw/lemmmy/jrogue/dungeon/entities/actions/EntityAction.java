package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public abstract class EntityAction {
	private Dungeon dungeon;
	private Entity entity;
	
	private ActionCallback callback;
	
	public EntityAction(Dungeon dungeon, Entity entity) {
		this(dungeon, entity, null);
	}
	
	public EntityAction(Dungeon dungeon, Entity entity, ActionCallback callback) {
		this.dungeon = dungeon;
		this.entity = entity;
		
		this.callback = callback;
	}
	
	public abstract void execute();
	
	public Dungeon getDungeon() {
		return dungeon;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void runBeforeRunCallback() {
		if (callback != null) {
			callback.beforeRun();
		}
	}
	
	public void runOnCompleteCallback() {
		if (callback != null) {
			callback.onComplete();
		}
	}
	
	public abstract static class ActionCallback {
		public void beforeRun() {}
		
		public void onComplete() {}
	}
}
