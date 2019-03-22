package jr.dungeon.entities.monsters.ai.stateful.generic;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.tiles.Tile;
import jr.dungeon.tiles.TileType;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.stream.Collectors;

public class StateLurk extends AIState<StatefulAI> {
	private static final int DEFAULT_LURK_RADIUS = 7;
	private static final float DEFAULT_LURK_PROBABILITY = 0.8f;
	
	@Expose private Point dest;
	
	@Expose @Getter	@Setter private int lurkRadius = DEFAULT_LURK_RADIUS;
	@Expose @Getter	@Setter private float lurkMoveProbability = DEFAULT_LURK_PROBABILITY;
	@Expose @Getter	@Setter private Entity lurkTarget;
	
	public StateLurk(StatefulAI ai, int duration, int lurkRadius, float lurkMoveProbability) {
		super(ai, duration);
	}
	
	public StateLurk(StatefulAI ai, int duration, int lurkRadius) {
		this(ai, duration, lurkRadius, DEFAULT_LURK_PROBABILITY);
	}
	
	public StateLurk(StatefulAI ai, int duration) {
		this(ai, duration, DEFAULT_LURK_RADIUS, DEFAULT_LURK_PROBABILITY);
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
		Monster monster = getAI().getMonster();
		Entity target = lurkTarget != null ? lurkTarget : monster;
		
		if (RandomUtils.randomFloat() > lurkMoveProbability) return null;
		
		return RandomUtils.randomFrom(monster.getLevel().tileStore.getTilesInRadius(target.getPosition(), lurkRadius).stream()
			.filter(t -> t.getType().getSolidity() != TileType.Solidity.SOLID)
			.map(Tile::getPosition)
			.collect(Collectors.toList()));
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("dest", dest);
	}
}
