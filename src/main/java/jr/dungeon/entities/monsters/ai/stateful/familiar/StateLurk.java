package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.StateApproachTarget;
import jr.dungeon.entities.player.Player;
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
		
		if (getAI().distanceFromPlayer() > 4) {
			getAI().setCurrentState(new StateFollowPlayer(getAI(), 0));
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
		Player p = getAI().getMonster().getDungeon().getPlayer();
		
		return RandomUtils.randomFrom(m.getLevel().tileStore.getTilesInRadius(p.getPosition(), 4).stream()
			.filter(t -> t.getType().getSolidity() != TileType.Solidity.SOLID)
			.map(Tile::getPosition)
			.collect(Collectors.toList()));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		if (dest != null) {
			obj.put("dest", new JSONObject().put("x", dest.getX()).put("y", dest.getY()));
		}
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