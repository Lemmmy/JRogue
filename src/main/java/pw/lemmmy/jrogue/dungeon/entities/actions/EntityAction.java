package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;

public abstract class EntityAction {
	private final ActionCallback callback;
	
	public EntityAction(ActionCallback callback) {
		this.callback = callback;
	}
	
	public abstract void execute(Entity entity, Messenger msg);
	
	public void runBeforeRunCallback(Entity entity) {
		if (callback != null) {
			callback.beforeRun(entity);
		}
	}
	
	public void runOnCompleteCallback(Entity entity) {
		if (callback != null) {
			callback.onComplete(entity);
		}
	}
	
	public interface ActionCallback {
		default void onComplete(Entity entity) {}
		
		default void beforeRun(Entity entity) {}
	}
	
	@FunctionalInterface
	public interface CompleteCallback extends ActionCallback {
		@Override
		void onComplete(Entity entity);
	}
	
	public static class NoCallback implements ActionCallback {}
}
