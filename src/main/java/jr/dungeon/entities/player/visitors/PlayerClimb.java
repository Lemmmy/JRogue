package jr.dungeon.entities.player.visitors;

import jr.dungeon.Level;
import jr.dungeon.entities.events.EntityChangeLevelEvent;
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
		
		switch (tile.getType()) { // TODO: make this not hardcoded
			case TILE_ROOM_STAIRS_UP:
				player.getDungeon().You("ascend the stairs.");
				break;
			case TILE_ROOM_STAIRS_DOWN:
				player.getDungeon().You("descend the stairs.");
				break;
			case TILE_LADDER_UP:
				player.getDungeon().You("climb up the ladder.");
				break;
			case TILE_LADDER_DOWN:
				player.getDungeon().You("climb down the ladder.");
				break;
		}
		
		TileStateClimbable tsc = (TileStateClimbable) tile.getState();
		
		if (!tsc.getLinkedLevel().isPresent()) {
			tsc.generateLevel(tile, up);
		}
		
		if (tsc.getLinkedLevel().isPresent()) {
			Level level = tsc.getLinkedLevel().get();
			
			player.getDungeon().eventSystem.triggerEvent(new EntityChangeLevelEvent(
				player,
				tile,
				tsc.getLinkedLevel().get().tileStore.getTile(tsc.getDestX(), tsc.getDestY())
			));
			
			player.getDungeon().changeLevel(level, tsc.getDestX(), tsc.getDestY());
		}
	}
}
