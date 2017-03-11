package jr.dungeon.entities.actions;

import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.items.quaffable.ItemQuaffable;

/**
 * Quaff/drink from item action. For example, see {@link jr.dungeon.items.quaffable.potions.ItemPotion}.
 *
 * @see jr.dungeon.entities.actions.EntityAction
 */
public class ActionQuaffItem extends EntityAction {
	private final ItemQuaffable quaffable;
	
	/**
	 * Quaff/drink from item action. For example, see {@link jr.dungeon.items.quaffable.potions.ItemPotion}.
	 *
	 * @param item The quaffable item to quaff/drink from.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link jr.dungeon.entities.actions.EntityAction.ActionCallback}.
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
