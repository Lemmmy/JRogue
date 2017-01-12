package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionTeleport;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Path;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerTravelPathfind implements PlayerVisitor {
	private int tx, ty;
	
	public PlayerTravelPathfind(int tx, int ty) {
		this.tx = tx;
		this.ty = ty;
	}
	
	@Override
	public void visit(Player player) {
		Tile destTile = player.getLevel().getTile(tx, ty);
		
		if (destTile == null || !player.getLevel().isTileDiscovered(tx, ty)) {
			player.getDungeon().You("can't travel there.");
			return;
		}
		
		Path path = player.getPathfinder().findPath(
			player.getLevel(),
			player.getX(),
			player.getY(),
			tx,
			ty,
			50,
			true,
			new ArrayList<>()
		);
		
		Path pathTaken = new Path();
		
		if (path == null || path.getLength() == 0) {
			player.getDungeon().You("can't travel there.");
			return;
		}
		
		AtomicBoolean stop = new AtomicBoolean(false);
		AtomicInteger i = new AtomicInteger(0);
		
		path.forEach(step -> {
			i.incrementAndGet();
			
			if (stop.get()) { return; }
			if (player.getX() == step.getX() && player.getY() == step.getY()) { return; }
			
			if (step.getType().getSolidity() == TileType.Solidity.SOLID) {
				stop.set(true);
				return;
			}
			
			int oldX = player.getX();
			int oldY = player.getY();
			
			pathTaken.addStep(step);
			player.setAction(new ActionMove(step.getX(), step.getY(), new EntityAction.NoCallback()));
			player.getDungeon().turn();
			
			if (oldX == player.getX() && oldY == player.getY()) {
				stop.set(true);
				return;
			}
			
			if (i.get() > 2 && player.getLevel().getAdjacentMonsters(player.getX(), player.getY()).size() > 0) {
				stop.set(true);
			}
		});
		
		player.getDungeon().showPath(pathTaken);
	}
}
