package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Solidity;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.stream.Collectors;

@Registered(id="aiStateHumanoidFlee")
public class StateFlee extends AIState<StatefulAI> {
    @Expose private Point dest;
    
    public StateFlee(StatefulAI ai, int duration) {
        super(ai, duration);
    }
    
    @Override
    public void update() {
        super.update();
        
        Monster m = ai.getMonster();
        
        if (dest == null) {
            dest = getRandomDestination();
        }
        
        if (m.getPosition().equals(dest) || m.getPosition().equals(m.getLastPosition())) {
            ai.setCurrentState(null);
            ai.addSafePoint(m.getPosition());
        } else {
            ai.moveTowards(dest);
        }
    }
    
    private Point getRandomDestination() {
        val safePoint = ai.getSafePoint();
        Monster m = ai.getMonster();
        
        return safePoint.orElseGet(() -> RandomUtils
            .randomFrom(m.getLevel().tileStore.getTilesInRadius(m.getPosition(), 7).stream()
                .filter(t -> t.getType().getSolidity() != Solidity.SOLID)
                .map(t -> t.position)
                .collect(Collectors.toList())));
    }
    
    @Override
    public ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("dest", dest);
    }
}
