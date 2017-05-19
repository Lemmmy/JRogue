package jr.dungeon.entities.monsters.ai.stateful.generic;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.RandomUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.util.stream.Collectors;

public class StateLurk extends AIState<StatefulAI> {
	private Point dest;
	
	public StateLurk(StatefulAI ai, int duration) {
		super(ai, duration);
	}
	
	@Override
	public void update() {
		super.update();
		
		Monster m = getAI().getMonster();
		
		if (dest == null) dest = getRandomDestination();
		if (dest == null) return;
		
		if (m.getPosition().equals(dest) || m.getPosition().equals(m.getLastPosition())) {
			dest = getRandomDestination();
			
			Point safePoint = Point.getPoint(m.getX(), m.getY());
			getAI().addSafePoint(safePoint);
		}
		
		if (dest == null) return;
		
		getAI().moveTowards(dest);
	}
	
	private Point getRandomDestination() {
		Monster m = getAI().getMonster();
		Entity target = m;
		int r = getAI().getPersistence().optInt("lurkRadius", 7);
		float p = (float) getAI().getPersistence().optDouble("lurkMoveProbability", 0.8);
		
		if (RandomUtils.randomFloat() > p) return null;
		
		if (getAI().getPersistence().has("lurkTarget")) {
			String uuid = getAI().getPersistence().getString("lurkTarget");
			Entity e = m.getDungeon().getLevel().entityStore.getEntityByUUID(uuid);
			
			if (e != null) {
				target = e;
			}
		}
		
		return RandomUtils.randomFrom(m.getLevel().tileStore.getTilesInRadius(target.getPosition(), r).stream()
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
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("dest", dest);
	}
}
