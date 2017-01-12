package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;

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
		
		Tile tile = entity.getLevel().getTile(x, y);
		
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
		
		List<Entity> walkable = entity.getLevel().getWalkableEntitiesAt(x, y);
		walkable.forEach(e -> e.teleport((LivingEntity) entity, entity instanceof Player));
		
		runOnCompleteCallback(entity);
	}
}
