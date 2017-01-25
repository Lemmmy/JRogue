package jr.dungeon.entities.actions;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;

import java.util.List;

public class ActionTeleport extends EntityAction {
	private int x;
	private int y;
	
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
		walkable.forEach(e -> e.teleport((EntityLiving) entity, entity instanceof Player));
		
		runOnCompleteCallback(entity);
	}
}
