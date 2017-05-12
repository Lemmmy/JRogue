package jr.dungeon.entities.monsters.ai;

import jr.JRogue;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventListener;
import jr.dungeon.tiles.TileType;
import jr.utils.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Artificial 'intelligence' class. Used to automate movement and actions of monsters in the dungeon, for example
 * making them try to follow the player or attack them.
 *
 * @see jr.dungeon.entities.monsters.ai.stateful.StatefulAI
 */
public abstract class AI implements Serialisable, Persisting, EventListener {
	@NonNull @Getter private Monster monster;
	
	private AStarPathfinder pathfinder = new AStarPathfinder();
	private List<TileType> avoidTiles = new ArrayList<>();
	
	private JSONObject persistence = new JSONObject();
	
	public AI(Monster monster) {
		this.monster = monster;
		
		avoidTiles.add(TileType.TILE_TRAP);
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
	 * {@link #moveTowards(int, int) Moves towards} the specified point.
	 *
	 * @param point The position to {@link #moveTowards(int, int) move towards.}
	 */
	public void moveTowards(Point point) {
		moveTowards(point.getX(), point.getY());
	}
	
	/**
	 * Pathfinds towards the specified destX and destY coordinates.
	 *
	 * @param destX The X position to pathfind towards.
	 * @param destY The Y position to pathfind towards.
	 *
	 * @see AStarPathfinder
	 */
	public void moveTowards(int destX, int destY) {
		int sourceX = getMonster().getX();
		int sourceY = getMonster().getY();
		
		Path path = pathfinder.findPath(
			getMonster().getLevel(),
			sourceX,
			sourceY,
			destX,
			destY,
			getMonster().getVisibilityRange(),
			getMonster().canMoveDiagonally(),
			avoidTiles
		);
		
		if (path != null) {
			path.getSteps().stream()
				.filter(t -> t.getX() != sourceX || t.getY() != sourceY)
				.findFirst()
				.ifPresent(t -> getMonster().setAction(new ActionMove(
					t.getX(),
					t.getY(),
					new Action.NoCallback()
				)));
		}
	}
	
	/**
	 * Called every turn - this is the AI's turn to assign an action to the monster.
	 */
	public abstract void update();
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		
		serialisePersistence(obj);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		unserialisePersistence(obj);
	}
	
	@Override
	public JSONObject getPersistence() {
		return persistence;
	}
	
	/**
	 * Instantiates and unserialises an AI from serialised JSON.
	 *
	 * @param serialisedAI The previously serialised JSONObject containing the AI information.
	 * @param monster The {@link Monster} that hosts this AI.
	 *
	 * @return A fully unserialised AI instance.
	 */
	@SuppressWarnings("unchecked")
	public static AI createFromJSON(JSONObject serialisedAI, Monster monster) {
		String aiClassName = serialisedAI.getString("class");
		
		try {
			Class<? extends AI> aiClass = (Class<? extends AI>) Class.forName(aiClassName);
			Constructor<? extends AI> aiConstructor = aiClass.getConstructor(Monster.class);
			
			AI ai = aiConstructor.newInstance(monster);
			ai.unserialise(serialisedAI);
			return ai;
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown AI class {}", aiClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("AI class {} has no unserialisation constructor", aiClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading AI class {}", aiClassName);
			JRogue.getLogger().error(e);
		}
		
		return null;
	}
	
	public String toString() {
		return "";
	}
	
	public List<EventListener> getSubListeners() {
		return new ArrayList<>();
	}
}
