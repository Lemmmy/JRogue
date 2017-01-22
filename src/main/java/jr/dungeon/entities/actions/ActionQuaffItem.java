package jr.dungeon.entities.actions;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.items.quaffable.ItemQuaffable;

public class ActionQuaffItem extends EntityAction {
	private final ItemQuaffable quaffable;
	
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
