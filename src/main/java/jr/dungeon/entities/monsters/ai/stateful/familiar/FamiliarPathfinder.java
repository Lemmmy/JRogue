package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.Level;
import jr.dungeon.entities.monsters.ai.AStarPathfinder;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.utils.Point;

import java.util.concurrent.atomic.AtomicReference;

public class FamiliarPathfinder extends AStarPathfinder {
    @Override
    public float getHeuristicCost(Level level, Point a, Point b) {
        AtomicReference<Float> cost = new AtomicReference<>(super.getHeuristicCost(level, a, b));
        
        level.entityStore.getItemsAt(b)
            .filter(e -> e.getItem().getAspect(AspectBeatitude.class).isPresent())
            .filter(e -> ((AspectBeatitude) e.getItem().getAspect(AspectBeatitude.class).get()).getBeatitude()
                == AspectBeatitude.Beatitude.CURSED)
            .findFirst()
            .ifPresent(e -> cost.set(20f));
            
        return cost.get();
    }
}
