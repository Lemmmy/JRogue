package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.quaffable.ItemQuaffable;

public class ActionQuaffItem extends EntityAction {
	private final ItemQuaffable quaffable;
	
	public ActionQuaffItem(ItemQuaffable item, ActionCallback callback) {
		super(callback);
		this.quaffable = item;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		if (entity instanceof LivingEntity) {
			quaffable.quaff((LivingEntity) entity);
		}
		runOnCompleteCallback(entity);
	}
}
