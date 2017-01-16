package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.states.TileStateClimbable;

public class PlayerClimb implements PlayerVisitor {
	private Tile tile;
	private boolean up;
	
	public PlayerClimb(Tile tile, boolean up) {
		this.tile = tile;
		this.up = up;
	}
	
	@Override
	public void visit(Player player) {
		if (!tile.hasState() || !(tile.getState() instanceof TileStateClimbable)) {
			return;
		}
		
		TileStateClimbable tsc = (TileStateClimbable) tile.getState();
		
		if (!tsc.getLinkedLevel().isPresent()) {
			int depth = player.getLevel().getDepth() + (up ? 1 : -1);
			Level level = player.getDungeon().newLevel(depth, tile);
			level.processEntityQueues();
			tsc.setLinkedLevelUUID(level.getUUID());
			tsc.setDestPosition(level.getSpawnX(), level.getSpawnY());
		}
		
		if (tsc.getLinkedLevel().isPresent()) {
			Level level = tsc.getLinkedLevel().get();
			player.getDungeon().changeLevel(level, tsc.getDestX(), tsc.getDestY());
		}
	}
}
