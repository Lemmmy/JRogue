package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.MultiLineNoPrefixToStringStyle;
import jr.utils.Point;
import jr.utils.RandomUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.util.stream.Collectors;

public class StateLurk extends AIState {
	private Point dest;
	
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
		
		if (dest == null) {
			dest = getRandomDestination();
		}
		
		if (m.getPosition().equals(dest) || m.getPosition().equals(m.getLastPosition())) {
			dest = getRandomDestination();
			
			Point safePoint = Point.getPoint(m.getX(), m.getY());
			getAI().addSafePoint(safePoint);
		}
		
		getAI().moveTowards(dest);
	}
	
	private Point getRandomDestination() {
		Monster m = getAI().getMonster();
		
		return RandomUtils.randomFrom(m.getLevel().getTileStore().getTilesInRadius(m.getX(), m.getY(), 7).stream()
			.filter(t -> t.getType().getSolidity() != TileType.Solidity.SOLID)
			.map(Tile::getPosition)
			.collect(Collectors.toList()));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("dest", new JSONObject().put("x", dest.getX()).put("y", dest.getY()));
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (obj.has("dest")) {
			JSONObject serialisedPoint = obj.getJSONObject("dest");
			dest = Point.getPoint(serialisedPoint.optInt("x"), serialisedPoint.optInt("y"));
		}
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append("duration", getDuration())
			.append("turnsTaken", getTurnsTaken())
			.append("dest", dest)
			.toString();
	}
}
