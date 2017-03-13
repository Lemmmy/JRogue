package jr.dungeon.entities.actions;

import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Quaffable;

/**
 * Quaff/drink from entity action. For example, see {@link jr.dungeon.entities.decoration.EntityFountain}.
 *
 * @see Action
 */
public class ActionQuaffEntity extends Action {
	private final Quaffable quaffable;
	
	/**
	 * Quaff/drink from entity action. For example, see {@link jr.dungeon.entities.decoration.EntityFountain}.
	 *
	 * @param quaffable The quaffable entity to quaff/drink from.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link Action.ActionCallback}.
	 */
	public ActionQuaffEntity(Quaffable quaffable, ActionCallback callback) {
		super(callback);
		this.quaffable = quaffable;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		if (entity instanceof EntityLiving) {
			((Quaffable) quaffable).quaff((EntityLiving) entity);
		}
		runOnCompleteCallback(entity);
	}
}
