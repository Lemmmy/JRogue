package jr.dungeon.entities.monsters.ai.stateful;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.AI;
import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.utils.DebugToStringStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * A state for stateful AI. A state is a certain action with intrinsic information that occurs over a specified
 * duration of turns. The state will return to the AI's default state when complete.
 *
 * If the state duration is 0, it will run indefinitely, until it manually assigns another state to the AI.
 *
 * This AI system may be replaced soon.
 */
@Getter
@HasRegistry
public class AIState<T extends StatefulAI> implements Serialisable, EventListener {
	protected T ai;
	
	@Expose @Setter private int duration;
	@Expose @Setter private int turnsTaken = 0;
	
	/**
	 * @param ai The {@link StatefulAI} that hosts this state.
	 * @param duration How many turns the state should run for. 0 for indefinite.
	 */
	public AIState(T ai, int duration) {
		this.ai = ai;
		this.duration = duration;
	}
	
	public void setAI(T ai) {
		this.ai = ai;
	}
	
	/**
	 * Called every turn - this is the state's chance to assign an action, or assign another state.
	 */
	public void update() {
		turnsTaken = Math.max(0, turnsTaken - 1);
	}
	
	@Override
	public Set<Object> getListenerSelves() {
		val selves = new HashSet<>();
		selves.add(this);
		selves.add(ai);
		selves.add(ai.getMonster());
		return selves;
	}
	
	/**
	 * @return The {@link Monster}'s current {@link Dungeon}, or <code>null</code> if the monster
	 *         or {@link AI} is null.
	 */
	public Dungeon getDungeon() {
		return ai != null ? ai.getDungeon() : null;
	}
	
	/**
	 * @return The {@link Monster}'s current {@link Level}, or <code>null</code> if the monster
	 *         or {@link AI} is null.
	 */
	public Level getLevel() {
		return ai != null ? ai.getLevel() : null;
	}
	
	@Override
	public String toString() {
		return toStringBuilder().build();
	}
	
	public ToStringBuilder toStringBuilder() {
		return new ToStringBuilder(this, DebugToStringStyle.STYLE)
			.append("duration", duration)
			.append("turnsTaken", turnsTaken);
	}
}
