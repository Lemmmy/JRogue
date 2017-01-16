package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Quaffable;
import pw.lemmmy.jrogue.dungeon.items.quaffable.ItemQuaffable;

public class ActionQuaffEntity extends EntityAction {
	private final Entity quaffable;
	
	public ActionQuaffEntity(Entity quaffable, ActionCallback callback) {
		super(callback);
		this.quaffable = quaffable;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		if (entity instanceof LivingEntity) {
			((Quaffable) quaffable).quaff((LivingEntity) entity);
		}
		runOnCompleteCallback(entity);
	}
}
