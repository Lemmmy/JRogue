package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;

import java.util.List;

public class ActionTeleport extends EntityAction {
	private int x;
	private int y;

	public ActionTeleport(Dungeon dungeon, Entity entity, int x, int y) {
		super(dungeon, entity);

		this.x = x;
		this.y = y;
	}

	@Override
	public void execute() {
		runBeforeRunCallback();

		Tile tile = getEntity().getLevel().getTile(x, y);

		if (tile == null) {
			runOnCompleteCallback();

			return;
		}

		getEntity().setPosition(x, y);

		if (getEntity() instanceof Player) {
			if (tile.getType().onWalk() != null) {
				getDungeon().log(tile.getType().onWalk());
			}
		}

		List<Entity> walkable = getEntity().getLevel().getWalkableEntitiesAt(x, y);
		walkable.forEach(e -> e.walk((LivingEntity) getEntity(), getEntity() instanceof Player)); // TODO: change to teleport

		runOnCompleteCallback();
	}
}
