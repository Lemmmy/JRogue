package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public class ActionMove extends EntityAction {
	private int x;
	private int y;

	public ActionMove(Entity entity, int x, int y) {
		super(entity);

		this.x = x;
		this.y = y;
	}

	@Override
	public int getSpeed() {
		return entity instanceof LivingEntity ? ((LivingEntity) entity).getMovementSpeed() : 0;
	}

	@Override
	public void execute() {
		entity.setPosition(x, y);
	}
}
