package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Tile;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;

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
		getEntity().setPosition(x, y);

		if (getEntity() instanceof Player) {
			Tile tile = getEntity().getLevel().getTileInfo(x, y);

			if (tile.getType().onWalk() != null) {
				getDungeon().log(tile.getType().onWalk());
			}
		}
	}
}
