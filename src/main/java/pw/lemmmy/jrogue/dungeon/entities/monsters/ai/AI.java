package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import pw.lemmmy.jrogue.dungeon.entities.Path;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class AI {
	private AStarPathfinder pathfinder = new AStarPathfinder();

	private Monster monster;

	private List<TileType> avoidTiles = new ArrayList<>();

	public AI(Monster monster) {
		this.monster = monster;
	}

	public void addAvoidTile(TileType tileType) {
		avoidTiles.add(tileType);
	}

	protected boolean canMoveTo(int x, int y) {
		return !(x < 0 || x > monster.getLevel().getWidth() ||
			y < 0 || y > monster.getLevel().getHeight()) &&
			monster.getLevel().getTileType(x, y).getSolidity() != TileType.Solidity.SOLID;
	}

	protected boolean canMoveTowardsPlayer() {
		return distanceFromPlayer() < monster.getVisibilityRange();
	}

	protected float distanceFromPlayer() {
		return Utils.distance(
			(float) monster.getX(), (float) monster.getY(),
			(float) monster.getDungeon().getPlayer().getX(), (float) monster.getDungeon().getPlayer().getY()
		);
	}

	protected boolean canMeleeAttackPlayer() {
		return monster.canMeleeAttack() && isAdjacentToPlayer();
	}

	protected boolean isAdjacentToPlayer() {
		Player player = monster.getDungeon().getPlayer();

		return ((player.getX() == monster.getX() ||
			player.getX() == monster.getX() - 1 ||
			player.getX() == monster.getX() + 1) &&
			(player.getY() == monster.getY() ||
				player.getY() == monster.getY() - 1 ||
				player.getY() == monster.getY() + 1));
	}

	protected void meleeAttackPlayer() {
		monster.meleeAttackPlayer();
	}

	protected void rangedAttackPlayer() {
		monster.meleeAttackPlayer();
	}

	protected void magicAttackPlayer() {
		monster.meleeAttackPlayer();
	}

	protected void moveTowardsPlayer() {
		Player player = monster.getDungeon().getPlayer();

		moveTowards(player.getX(), player.getY());
	}

	protected void moveTowards(int destX, int destY) {
		Path path = pathfinder.findPath(
			getMonster().getLevel(),
			getMonster().getX(),
			getMonster().getY(),
			destX,
			destY,
			getMonster().getVisibilityRange(),
			getMonster().canMoveDiagonally(),
			avoidTiles
		);

		if (path != null) {
			getMonster().setAction(new ActionMove(
									   getMonster().getDungeon(), getMonster(),
									   path.getStep(1).getX(), path.getStep(1).getY()
								   )
			);
		}
	}

	public Monster getMonster() {
		return monster;
	}

	public abstract void update();
}
