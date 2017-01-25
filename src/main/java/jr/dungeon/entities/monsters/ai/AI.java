package jr.dungeon.entities.monsters.ai;

import jr.JRogue;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.utils.Path;
import org.json.JSONObject;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.actions.EntityAction;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.tiles.TileType;
import jr.utils.Serialisable;
import jr.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class AI implements Serialisable {
	private AStarPathfinder pathfinder = new AStarPathfinder();
	
	private Monster monster;
	
	private List<TileType> avoidTiles = new ArrayList<>();
	
	public AI(Monster monster) {
		this.monster = monster;
	}
	
	public void addAvoidTile(TileType tileType) {
		avoidTiles.add(tileType);
	}
	
	public boolean canMoveTo(int x, int y) {
		return !(x < 0 || x > monster.getLevel().getWidth() ||
			y < 0 || y > monster.getLevel().getHeight()) &&
			monster.getLevel().tileStore.getTileType(x, y).getSolidity() != TileType.Solidity.SOLID;
	}
	
	public boolean canMoveTowardsPlayer() {
		return distanceFromPlayer() < monster.getVisibilityRange();
	}
	
	public float distanceFromPlayer() {
		return Utils.distance(
			(float) monster.getX(), (float) monster.getY(),
			(float) monster.getDungeon().getPlayer().getX(), (float) monster.getDungeon().getPlayer().getY()
		);
	}
	
	public boolean canMeleeAttack(EntityLiving target) {
		return monster.canMeleeAttack() && isAdjacentTo(target);
	}
	
	public boolean canMeleeAttackPlayer() {
		return canMeleeAttack(monster.getDungeon().getPlayer());
	}
	
	public boolean isAdjacentTo(EntityLiving target) {
		int ax = target.getX();
		int ay = target.getY();
		int bx = monster.getX();
		int by = monster.getY();
		
		return Utils.chebyshevDistance(ax, ay, bx, by) <= 1;
	}
	
	public boolean isAdjacentToPlayer() {
		Player player = monster.getDungeon().getPlayer();
		
		return isAdjacentTo(player);
	}
	
	public void meleeAttack(EntityLiving target) {
		monster.meleeAttack(target);
	}
	
	public void rangedAttack(EntityLiving target) {
		monster.rangedAttack(target);
	}
	
	public void magicAttack(EntityLiving target) {
		monster.magicAttack(target);
	}
	
	public void meleeAttackPlayer() {
		meleeAttack(monster.getDungeon().getPlayer());
	}
	
	public void rangedAttackPlayer() {
		rangedAttack(monster.getDungeon().getPlayer());
	}
	
	public void magicAttackPlayer() {
		magicAttack(monster.getDungeon().getPlayer());
	}
	
	public void moveTowardsPlayer() {
		Player player = monster.getDungeon().getPlayer();
		
		moveTowards(player.getX(), player.getY());
	}
	
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
					new EntityAction.NoCallback()
				)));
		}
	}
	
	public Monster getMonster() {
		return monster;
	}
	
	public abstract void update();
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		
	}
		
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
}
