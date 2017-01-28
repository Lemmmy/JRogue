package jr.dungeon.entities.actions;

import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Quaffable;

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
