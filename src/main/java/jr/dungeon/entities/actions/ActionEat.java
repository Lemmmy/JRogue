package jr.dungeon.entities.actions;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Messenger;
import jr.dungeon.items.comestibles.ItemComestible;

/**
 * Eat action. Takes 1 turn to consume a part of a comestible.
 *
 * @see Action
 */
public class ActionEat extends Action {
	private final ItemComestible item;
	
	/**
	 * Eat action. Takes 1 turn to consume a part of a comestible.
	 *
	 * @param item The comestible to eat.
	 * @param callback {@link Action.ActionCallback Callback} to call when action-related events occur.
	 */
	public ActionEat(ItemComestible item, ActionCallback callback) {
		super(callback);
		this.item = item;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			player.defaultVisitors.consume(item);
			
			runOnCompleteCallback(entity);
		}
	}
}
