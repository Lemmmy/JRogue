package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.Quaffable;

public class ActionQuaffEntity extends EntityAction {
	private final Entity quaffable;
	
	public ActionQuaffEntity(Entity quaffable, ActionCallback callback) {
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
