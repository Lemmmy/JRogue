package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMove;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;
import pw.lemmmy.jrogue.utils.Path;
import pw.lemmmy.jrogue.utils.Serialisable;
import pw.lemmmy.jrogue.utils.Utils;

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
			monster.getLevel().getTileType(x, y).getSolidity() != TileType.Solidity.SOLID;
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
	
	public boolean canMeleeAttackPlayer() {
		return monster.canMeleeAttack() && isAdjacentToPlayer();
	}
	
	public boolean isAdjacentToPlayer() {
		Player player = monster.getDungeon().getPlayer();
		
		return (player.getX() == monster.getX() ||
			player.getX() == monster.getX() - 1 ||
			player.getX() == monster.getX() + 1) &&
			(player.getY() == monster.getY() ||
			player.getY() == monster.getY() - 1 ||
			player.getY() == monster.getY() + 1);
	}
	
	public void meleeAttackPlayer() {
		monster.meleeAttackPlayer();
	}
	
	public void rangedAttackPlayer() {
		monster.meleeAttackPlayer();
	}
	
	public void magicAttackPlayer() {
		monster.meleeAttackPlayer();
	}
	
	public void moveTowardsPlayer() {
		Player player = monster.getDungeon().getPlayer();
		
		moveTowards(player.getX(), player.getY());
	}
	
	public void moveTowards(int destX, int destY) {
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
			getMonster().setAction(
				new ActionMove(path.getStep(1).getX(), path.getStep(1).getY(), new EntityAction.NoCallback()));
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
}
