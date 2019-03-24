package jr.dungeon.entities.monsters.ai;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.dungeon.tiles.TileType;
import jr.utils.DebugToStringStyle;
import jr.utils.Path;
import jr.utils.Point;
import jr.utils.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Artificial 'intelligence' class. Used to automate movement and actions of monsters in the dungeon, for example
 * making them try to follow the player or attack them.
 *
 * @see jr.dungeon.entities.monsters.ai.stateful.StatefulAI
 */
@HasRegistry
public abstract class AI implements Serialisable, EventListener {
	@NonNull @Getter @Setter private Monster monster;
	
	@Getter @Setter	private AStarPathfinder pathfinder = new AStarPathfinder();
	@Expose private List<TileType> avoidTiles = new ArrayList<>();
	
	protected int suppressTurns = 0;
	
	public AI(Monster monster) {
		this.monster = monster;
		
		avoidTiles.add(TileType.TILE_TRAP);
	}
	
	protected AI() {} // deserialisation constructor
	
	@Override
	public void afterDeserialise() {
		pathfinder = new AStarPathfinder();
	}
	
	/**
	 * Adds a tile to the list of tiles to avoid during pathfinding. The AI will absolutely never try to step on this
	 * tile.
	 *
	 * @param tileType The tile type to add.
	 */
	public void addAvoidTile(TileType tileType) {
		avoidTiles.add(tileType);
	}
	
	/**
	 * @param x The X position to check.
	 * @param y The Y position to check.
	 *
	 * @return Whether or not the AI can move to this position - checks if its inside the map and if its not solid.
	 */
	public boolean canMoveTo(int x, int y) {
		return !(x < 0 || x > monster.getLevel().getWidth() ||
			y < 0 || y > monster.getLevel().getHeight()) &&
			monster.getLevel().tileStore.getTileType(x, y).getSolidity() != TileType.Solidity.SOLID;
	}

	public boolean canMoveTo(Point p) {
		return canMoveTo(p.getX(), p.getY());
	}

	/**
	 * @return Whether or not the player is within the monster's {@link Monster#getVisibilityRange() visibility range}.
	 */
	public boolean canMoveTowardsPlayer() {
		return distanceFromPlayer() < monster.getVisibilityRange();
	}
	
	/**
	 * @param entity Entity
	 *
	 * @return Returns the linear distance between the entity and the monster.
	 */
	public float distanceFrom(Entity entity) {
		return Utils.distance(
			(float) monster.getX(), (float) monster.getY(),
			(float) entity.getX(), (float) entity.getY()
		);
	}
	
	/**
	 * @return Returns the linear distance between the player and the monster.
	 */
	public float distanceFromPlayer() {
		return distanceFrom(monster.getDungeon().getPlayer());
	}
	
	/**
	 * @param target The target to attack.'
	 *
	 * @return Whether or not the monster is able to melee attack, and if it is adjacent to the target.
	 */
	public boolean canMeleeAttack(EntityLiving target) {
		return monster.canMeleeAttack() && isAdjacentTo(target);
	}
	
	/**
	 * @return Whether or not the monster is able to melee attack the player.
	 */
	public boolean canMeleeAttackPlayer() {
		return canMeleeAttack(monster.getDungeon().getPlayer());
	}
	
	/**
	 * @param target The target to check adjacency to.
	 *
	 * @return Whether or not the monster is adjacent to the target - checks for a
	 * {@link Utils#chebyshevDistance(int, int, int, int) Chebyshev distance} of 1 or less.
	 */
	public boolean isAdjacentTo(EntityLiving target) {
		int ax = target.getX();
		int ay = target.getY();
		int bx = monster.getX();
		int by = monster.getY();
		
		return Utils.chebyshevDistance(ax, ay, bx, by) <= 1;
	}
	
	/**
	 * @return Whether or not the monster is {#isAdjacentTo adjacent to} the player.
	 */
	public boolean isAdjacentToPlayer() {
		Player player = monster.getDungeon().getPlayer();
		
		return isAdjacentTo(player);
	}
	
	/**
	 * Melee attacks the target.
	 *
	 * @param target The target to attack.
	 */
	public void meleeAttack(EntityLiving target) {
		monster.meleeAttack(target);
	}
	
	/**
	 * Ranged attacks the target.
	 *
	 * @param target The target to attack.
	 */
	public void rangedAttack(EntityLiving target) {
		monster.rangedAttack(target);
	}
	
	/**
	 * Magic attacks the target.
	 *
	 * @param target The target to attack.
	 */
	public void magicAttack(EntityLiving target) {
		monster.magicAttack(target);
	}
	
	/**
	 * Melee attacks the player.
	 */
	public void meleeAttackPlayer() {
		meleeAttack(monster.getDungeon().getPlayer());
	}
	
	/**
	 * Ranged attacks the player.
	 */
	public void rangedAttackPlayer() {
		rangedAttack(monster.getDungeon().getPlayer());
	}
	
	/**
	 * Magic attacks the player.
	 */
	public void magicAttackPlayer() {
		magicAttack(monster.getDungeon().getPlayer());
	}
	
	/**
	 * {@link #moveTowards(Entity) Moves towards} the player.
	 */
	public void moveTowardsPlayer() {
		Player player = monster.getDungeon().getPlayer();
		
		moveTowards(player);
	}
	
	/**
	 * {@link #moveTowards(Point) Moves towards} the entity.
	 *
	 * @param entity The entity to {@link #moveTowards(Point) move towards.}
	 */
	public void moveTowards(Entity entity) {
		moveTowards(entity.getPosition());
	}
	
	/**
	 * {@link #moveTowards(Point) Moves towards} the {@link Entity} serialised within an
	 * {@link EntityReference}.
	 *
	 * @param entity The entity to {@link #moveTowards(Point) move towards.}
	 */
	public void moveTowards(EntityReference entity) {
		if (!entity.isSet()) return;
		moveTowards(entity.get(getLevel()).getPosition());
	}
	
	/**
	 * {@link #moveTowards(int, int) Moves towards} the specified point.
	 *
	 * @param point The position to {@link #moveTowards(int, int) move towards.}
	 */
	public void moveTowards(Point point) {
		moveTowards(point.getX(), point.getY());
	}
	
	/**
	 * Finds a path towards the specified destX and destY coordinates.
	 *
	 * @param destX The X position to pathfind towards.
	 * @param destY The Y position to pathfind towards.
	 *
	 * @see AStarPathfinder
	 */
	public void moveTowards(int destX, int destY) {
		if (monster.hasAction()) return;
		
		int sourceX = getMonster().getX();
		int sourceY = getMonster().getY();
		
		Path path = pathfinder.findPath(
			monster.getLevel(),
			sourceX,
			sourceY,
			destX,
			destY,
			monster.getVisibilityRange(),
			monster.canMoveDiagonally(),
			avoidTiles
		);
		
		if (path != null) {
			path.getSteps().stream()
				.filter(t -> t.getX() != sourceX || t.getY() != sourceY)
				.findFirst()
				.ifPresent(t -> monster.setAction(new ActionMove(
					t.getX(),
					t.getY(),
					new Action.NoCallback()
				)));
		}
	}
	
	public boolean canReach(Entity e) {
		Path path = pathfinder.findPath(
			monster.getLevel(),
			getMonster().getX(),
			getMonster().getY(),
			e.getX(),
			e.getY(),
			monster.getVisibilityRange(),
			monster.canMoveDiagonally(),
			avoidTiles
		);
		
		return path != null;
	}
	
	/**
	 * Called every turn - this is the AI's turn to assign an action to the monster.
	 */
	public abstract void update();
	
	@Override
	public Set<Object> getListenerSelves() {
		val selves = new HashSet<>();
		selves.add(this);
		selves.add(monster);
		return selves;
	}
	
	@Override
	public String toString() {
		return toStringBuilder().build();
	}
	
	public ToStringBuilder toStringBuilder() {
		return new ToStringBuilder(this, DebugToStringStyle.STYLE);
	}
	
	public List<EventListener> getSubListeners() {
		return new ArrayList<>();
	}
	
	public void suppress(int turns) {
		suppressTurns = turns;
	}
	
	/**
	 * @return The {@link Monster}'s current {@link Dungeon}, or <code>null</code> if the monster is null.
	 */
	public Dungeon getDungeon() {
		return getMonster() != null ? getMonster().getDungeon() : null;
	}
	
	/**
	 * @return The {@link Monster}'s current {@link Level}, or <code>null</code> if the monster is null.
	 */
	public Level getLevel() {
		return getMonster() != null ? getMonster().getLevel() : null;
	}
}
