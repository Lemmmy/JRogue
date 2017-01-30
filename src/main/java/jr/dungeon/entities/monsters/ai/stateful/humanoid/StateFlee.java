package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.MultiLineNoPrefixToStringStyle;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.util.stream.Collectors;

public class StateFlee extends AIState {
	private Point dest;
	
	public StateFlee(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		Monster m = getAI().getMonster();
		
		if (dest == null) {
			dest = getRandomDestination();
		}
		
		if (m.getPosition() == dest || m.getPosition() == m.getLastPosition()) {
			getAI().setCurrentState(null);
			
			Point safePoint = Point.getPoint(m.getX(), m.getY());
			getAI().addSafePoint(safePoint);
		} else {
			getAI().moveTowards(dest);
		}
	}
	
	private Point getRandomDestination() {
		val safePoint = getAI().getSafePoint();
		Monster m = getAI().getMonster();
		
		if (safePoint.isPresent()) {
			return safePoint.get();
		} else {
			return RandomUtils.randomFrom(m.getLevel().getTileStore().getTilesInRadius(m.getX(), m.getY(), 7).stream()
				.filter(t -> t.getType().getSolidity() != TileType.Solidity.SOLID)
				.map(Tile::getPosition)
				.collect(Collectors.toList()));
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("dest", dest);
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
