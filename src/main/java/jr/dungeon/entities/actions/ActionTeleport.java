package jr.dungeon.entities.actions;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Messenger;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;

/**
 * Teleport action.
 *
 * @see Action
 */
public class ActionTeleport extends Action {
	private Point position;
	
	/**
	 * Teleport action.
	 *
	 * @param position The point to teleport to.
	 * @param callback {@link Action.ActionCallback Callback} to call when action-related events occur.
	 */
	public ActionTeleport(Point position, ActionCallback callback) {
		super(callback);
		this.position = position;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		Tile tile = entity.getLevel().tileStore.getTile(position);
		
		if (tile == null) {
			runOnCompleteCallback(entity);
			return;
		}
		
		entity.setPosition(position);
		
		if (entity instanceof Player) {
			if (tile.getType().onWalk() != null) {
				msg.log(tile.getType().onWalk());
			}
		}
		
		entity.getLevel().entityStore.getWalkableEntitiesAt(position)
			.forEach(e -> e.teleport((EntityLiving) entity));
		
		runOnCompleteCallback(entity);
	}
}
