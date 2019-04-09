package jr.dungeon.entities.monsters.ai.stateful.generic;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AIState;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.tiles.Solidity;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.stream.Collectors;

@Registered(id="aiStateLurk")
public class StateLurk extends AIState<StatefulAI> {
    private static final int DEFAULT_LURK_RADIUS = 7;
    private static final float DEFAULT_LURK_PROBABILITY = 0.8f;
    
    @Expose private Point dest;
    
    @Expose @Getter    @Setter private int lurkRadius = DEFAULT_LURK_RADIUS;
    @Expose @Getter    @Setter private float lurkMoveProbability = DEFAULT_LURK_PROBABILITY;
    @Expose @Getter    private EntityReference<Entity> lurkTarget = new EntityReference<>();
    
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
    public void afterDeserialise() {
        super.afterDeserialise();
        if (lurkTarget == null) lurkTarget = new EntityReference<>();
    }
    
    @Override
    public void update() {
        super.update();
        
        Monster m = ai.getMonster();
        
        if (dest == null) dest = getRandomDestination();
        if (dest == null) return;
        
        if (m.getPosition().equals(dest) || m.getPosition().equals(m.getLastPosition())) {
            dest = getRandomDestination();
            ai.addSafePoint(m.getPosition());
        }
        
        if (dest == null) return;
        
        ai.moveTowards(dest);
    }
    
    private Point getRandomDestination() {
        Monster monster = ai.getMonster();
        Entity target = lurkTarget.orElse(getLevel(), monster);
        
        if (RandomUtils.randomFloat() > lurkMoveProbability) return null;
        
        return RandomUtils.randomFrom(monster.getLevel().tileStore.getTilesInRadius(target.getPosition(), lurkRadius).stream()
            .filter(t -> t.getType().getSolidity() != Solidity.SOLID)
            .map(t -> t.position)
            .collect(Collectors.toList()));
    }
    
    @Override
    public ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("dest", dest);
    }
}
