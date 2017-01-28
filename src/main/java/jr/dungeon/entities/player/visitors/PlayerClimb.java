package jr.dungeon.entities.player.visitors;

import jr.dungeon.Level;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.states.TileStateClimbable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerClimb implements PlayerVisitor {
	private Tile tile;
	private boolean up;
	
	@Override
	public void visit(Player player) {
		if (!tile.hasState() || !(tile.getState() instanceof TileStateClimbable)) {
			return;
		}
		
		switch (tile.getType()) {
			case TILE_ROOM_STAIRS_UP:
				player.getDungeon().You("ascend the stairs.");
				break;
			case TILE_ROOM_STAIRS_DOWN:
				player.getDungeon().You("descend the stairs.");
				break;
			case TILE_ROOM_LADDER_UP:
				player.getDungeon().You("climb up the ladder.");
				break;
			case TILE_ROOM_LADDER_DOWN:
				player.getDungeon().You("climb down the ladder.");
				break;
		}
		
		TileStateClimbable tsc = (TileStateClimbable) tile.getState();
		
		if (!tsc.getLinkedLevel().isPresent()) {
			int depth = player.getLevel().getDepth() + (up ? 1 : -1);
			Level level = player.getDungeon().newLevel(depth, tile, tsc.getGeneratorClass());
			level.getEntityStore().processEntityQueues();
			tsc.setLinkedLevelUUID(level.getUUID());
			tsc.setDestPosition(level.getSpawnX(), level.getSpawnY());
		}
		
		if (tsc.getLinkedLevel().isPresent()) {
			Level level = tsc.getLinkedLevel().get();
			player.getDungeon().changeLevel(level, tsc.getDestX(), tsc.getDestY());
		}
	}
}
