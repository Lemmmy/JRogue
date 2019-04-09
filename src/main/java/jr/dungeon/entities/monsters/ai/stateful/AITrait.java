package jr.dungeon.entities.monsters.ai.stateful;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.utils.DebugToStringStyle;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
 */
@Getter
@HasRegistry
public abstract class AITrait<T extends StatefulAI> implements Serialisable, EventListener {
    protected T ai;
    
    /**
     * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
     *
     * @param ai The {@link StatefulAI} that hosts this trait.
     */
    public AITrait(T ai) {
        setAI(ai);
    }
    
    protected AITrait() {} // deserialisation constructor
    
    public void setAI(T ai) {
        this.ai = ai;
    }
    
    /**
     * Called every turn when the AI's monster gets a turn to move.
     */
    public abstract void update();
    
    public int getPriority() {
        return 0;
    }
    
    @Override
    public Set<Object> getListenerSelves() {
        val selves = new HashSet<>();
        selves.add(this);
        selves.add(ai);
        selves.add(getMonster());
        return selves;
    }
    
    public Monster getMonster() {
        return ai.getMonster();
    }
    
    /**
     * @return The {@link Monster}'s current {@link Dungeon}, or {@code null} if the monster or {@link AI} is null.
     */
    public Dungeon getDungeon() {
        return ai != null ? ai.getDungeon() : null;
    }
    
    /**
     * @return The {@link Monster}'s current {@link Level}, or {@code null} if the monster or {@link AI} is null.
     */
    public Level getLevel() {
        return ai != null ? ai.getLevel() : null;
    }
    
    @Override
    public String toString() {
        return toStringBuilder().build();
    }
    
    public ToStringBuilder toStringBuilder() {
        return new ToStringBuilder(this, DebugToStringStyle.STYLE);
    }
}
