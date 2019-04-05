package jr.dungeon.entities.actions;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.io.Messenger;
import jr.dungeon.items.quaffable.ItemQuaffable;

/**
 * Quaff/drink from item action. For example, see {@link jr.dungeon.items.quaffable.potions.ItemPotion}.
 *
 * @see Action
 */
public class ActionQuaffItem extends Action {
	private final ItemQuaffable quaffable;
	
	/**
	 * Quaff/drink from item action. For example, see {@link jr.dungeon.items.quaffable.potions.ItemPotion}.
	 *
	 * @param item The quaffable item to quaff/drink from.
	 * @param callback {@link Action.ActionCallback Callback} to call when action-related events occur.
	 */
	public ActionQuaffItem(ItemQuaffable item, ActionCallback callback) {
		super(callback);
		this.quaffable = item;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		if (entity instanceof EntityLiving) {
			quaffable.quaff((EntityLiving) entity);
		}
		runOnCompleteCallback(entity);
	}
}
