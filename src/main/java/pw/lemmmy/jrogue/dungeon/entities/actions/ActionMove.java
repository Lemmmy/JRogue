package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Tile;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;

import java.util.List;

public class ActionMove extends EntityAction {
	private int x;
	private int y;

	public ActionMove(Dungeon dungeon, Entity entity, int x, int y) {
		super(dungeon, entity);

		this.x = x;
		this.y = y;
	}

	@Override
	public void execute() {
		if (getEntity().getLevel().getUnwalkableEntitiesAt(x, y).size() > 0) {
			return;
		}

		getEntity().setPosition(x, y);

		if (getEntity() instanceof Player) {
			Tile tile = getEntity().getLevel().getTileInfo(x, y);

			if (tile.getType().onWalk() != null) {
				getDungeon().log(tile.getType().onWalk());
			}
		}

		List<Entity> entities = getEntity().getLevel().getWalkableEntitiesAt(x, y);

		if (entities.size() > 0) {
			for (Entity entity : entities) {
				entity.walk((LivingEntity) getEntity(), getEntity() instanceof Player);
			}
		}
	}
}
