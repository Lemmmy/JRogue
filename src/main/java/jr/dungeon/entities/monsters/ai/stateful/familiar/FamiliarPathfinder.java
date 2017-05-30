package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.Level;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.monsters.ai.AStarPathfinder;
import jr.dungeon.items.identity.AspectBeatitude;

import java.util.concurrent.atomic.AtomicReference;

public class FamiliarPathfinder extends AStarPathfinder {
	@Override
	public float getHeuristicCost(Level level, int ax, int ay, int bx, int by) {
		AtomicReference<Float> cost = new AtomicReference<>(super.getHeuristicCost(level, ax, ay, bx, by));
		
		level.entityStore.getEntitiesAt(bx, by).stream()
			.filter(EntityItem.class::isInstance)
			.map(EntityItem.class::cast)
			.filter(e -> e.getItem().getAspect(AspectBeatitude.class).isPresent())
			.filter(e -> ((AspectBeatitude) e.getItem().getAspect(AspectBeatitude.class).get()).getBeatitude()
				== AspectBeatitude.Beatitude.CURSED)
			.findFirst()
			.ifPresent(e -> cost.set(20f));
			
		return cost.get();
	}
}
