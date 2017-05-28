package jr.dungeon.entities.actions;

import jr.dungeon.io.Messenger;
import jr.dungeon.entities.Entity;
import jr.utils.DebugToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An action which a {@link jr.dungeon.entities.EntityTurnBased turn-based entity} should perform during a turn.
 */
public abstract class Action {
	private final ActionCallback callback;
	
	/**
	 * An action which a turn-based entity should perform during a turn.
	 *
	 * @param callback The {@link ActionCallback} which will be called when action-related events occur.
	 */
	public Action(ActionCallback callback) {
		this.callback = callback;
	}
	
	/**
	 * Called when the {@link jr.dungeon.entities.EntityTurnBased turn-based entity} gets a chance to perform the
	 * action.
	 *
	 * @param entity The {@link Entity} which is performing the action.
	 * @param msg The {@link Messenger} which you can send information to about the action. This is typically the
	 * {@link jr.dungeon.Dungeon}. Passed for convenience.
	 */
	public abstract void execute(Entity entity, Messenger msg);
	
	/**
	 * Calls the {@link ActionCallback}'s {@link ActionCallback#beforeRun(Entity)} callback.
	 *
 	 * @param entity The entity from {@link #execute(Entity, Messenger)}.
	 */
	public void runBeforeRunCallback(Entity entity) {
		if (callback != null) {
			callback.beforeRun(entity);
		}
	}
	
	/**
	 * Calls the {@link ActionCallback}'s {@link ActionCallback#onComplete(Entity)}} callback.
	 *
	 * @param entity The entity from {@link #execute(Entity, Messenger)}.
	 */
	public void runOnCompleteCallback(Entity entity) {
		if (callback != null) {
			callback.onComplete(entity);
		}
	}
	
	@Override
	public String toString() {
		return toStringBuilder().build();
	}
	
	public ToStringBuilder toStringBuilder() {
		return new ToStringBuilder(this, DebugToStringStyle.STYLE);
	}
	
	/**
	 * Custom action callback handler. Use this to perform anything when something happens related to the action.
	 */
	public interface ActionCallback {
		/**
		 * Called when the action has been completed. The turn has not necessarily been completed yet.
		 *
		 * @param entity The entity which performed the action.
		 */
		default void onComplete(Entity entity) {}
		
		/**
		 * Called before the action has begun. The turn has not necessarily been completed yet.
		 *
		 * @param entity The entity which is going to perform the action.
		 */
		default void beforeRun(Entity entity) {}
	}
	
	/**
	 * An {@link ActionCallback} handler with only the {@link ActionCallback#onComplete(Entity) onComplete} callback
	 * for convenience.
	 *
	 * This is a {@link FunctionalInterface}, so it can be used with a lambda function. Example usage:
	 * <pre><code>
	 * entity.setAction(new ActionX(..., (Action.CompleteCallback) entity -&gt; { doSomething() })
	 * </code></pre>
 	 */
	@FunctionalInterface
	public interface CompleteCallback extends ActionCallback {
		@Override
		void onComplete(Entity entity);
	}
	
	
	@FunctionalInterface
	public interface BeforeRunCallback extends ActionCallback {
		@Override
		void beforeRun(Entity entity);
	}
	
	/**
	 * An {@link ActionCallback} handler that does nothing for convenience. You can also pass <code>null</code> as the
	 * callback argument in most cases.
	 */
	public static class NoCallback implements ActionCallback {}
}
