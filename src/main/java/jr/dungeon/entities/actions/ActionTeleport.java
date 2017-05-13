package jr.dungeon.entities.actions;

import jr.dungeon.io.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;

import java.util.List;

/**
 * Teleport action.
 *
 * @see Action
 */
public class ActionTeleport extends Action {
	private int x;
	private int y;
	
	/**
	 * Teleport action.
	 *
	 * @param point The point to teleport to.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link Action.ActionCallback}.
	 */
	public ActionTeleport(Point point, ActionCallback callback) {
		this(point.getX(), point.getY(), callback);
	}
	
	/**
	 * Teleport action.
	 *
	 * @param x The X position to teleport to.
	 * @param y The Y position to teleport to.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link Action.ActionCallback}.
	 */
	public ActionTeleport(int x, int y, ActionCallback callback) {
		super(callback);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		Tile tile = entity.getLevel().tileStore.getTile(x, y);
		
		if (tile == null) {
			runOnCompleteCallback(entity);
			return;
		}
		
		entity.setPosition(x, y);
		
		if (entity instanceof Player) {
			if (tile.getType().onWalk() != null) {
				msg.log(tile.getType().onWalk());
			}
		}
		
		List<Entity> walkable = entity.getLevel().entityStore.getWalkableEntitiesAt(x, y);
		walkable.forEach(e -> e.teleport((EntityLiving) entity));
		
		runOnCompleteCallback(entity);
	}
}
