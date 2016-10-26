package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public class ActionMove extends EntityAction {
	private int x;
	private int y;

	public ActionMove(Dungeon dungeon, Entity entity, int x, int y) {
		super(dungeon, entity);

		this.x = x;
		this.y = y;
	}

	@Override
	public int getTurnsRequired() {
		return getEntity() instanceof LivingEntity ? ((LivingEntity) getEntity()).getMovementSpeed() : 0;
	}

	@Override
	public void execute() {
		getEntity().setPosition(x, y);
	}
}
