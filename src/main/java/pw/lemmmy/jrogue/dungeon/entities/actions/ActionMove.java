package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
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
		List<Entity> unwalkable = getEntity().getLevel().getUnwalkableEntitiesAt(x, y);

		if (unwalkable.size() > 0) {
			if (getEntity() instanceof Player) {
				Entity entity = unwalkable.get(0);

				if (entity.getLastX() != entity.getX() || entity.getLastY() != entity.getY()) {
					getDungeon().The("%s beats you to it!", entity.getName(false));
				}
			}

			return;
		}

		getEntity().setPosition(x, y);

		if (getEntity() instanceof Player) {
			Tile tile = getEntity().getLevel().getTile(x, y);

			if (tile.getType().onWalk() != null) {
				getDungeon().log(tile.getType().onWalk());
			}
		}

		List<Entity> walkable = getEntity().getLevel().getWalkableEntitiesAt(x, y);
		walkable.forEach(e -> e.walk((LivingEntity) getEntity(), getEntity() instanceof Player));
	}
}
