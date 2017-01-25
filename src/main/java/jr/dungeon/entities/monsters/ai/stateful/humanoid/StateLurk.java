package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.tiles.Tile;
import org.json.JSONObject;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.tiles.TileType;
import jr.utils.RandomUtils;

import java.util.stream.Collectors;

public class StateLurk extends AIState {
	private int destX, destY;
	
	public StateLurk(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (getAI().canSeeTarget()) {
			getAI().setCurrentState(new StateApproachTarget(getAI(), 0));
			return;
		}
		
		Monster m = getAI().getMonster();
		
		if (m.getX() == destX && m.getY() == destY || m.getX() == m.getLastX() && m.getY() == m.getLastY()) {
			Tile t = getRandomDestination();
			destX = t.getX();
			destY = t.getY();
		}
		
		getAI().moveTowards(destX, destY);
	}
	
	private Tile getRandomDestination() {
		Monster m = getAI().getMonster();
		
		return RandomUtils.randomFrom(m.getLevel().tileStore.getTilesInRadius(m.getX(), m.getY(), 7).stream()
			.filter(t -> t.getType().getSolidity() != TileType.Solidity.SOLID)
			.collect(Collectors.toList()));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("destX", destX);
		obj.put("destY", destY);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		destX = obj.optInt("destX", destX);
		destY = obj.optInt("destY", destY);
	}
}
