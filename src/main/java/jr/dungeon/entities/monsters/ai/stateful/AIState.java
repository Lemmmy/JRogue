package jr.dungeon.entities.monsters.ai.stateful;

import jr.JRogue;
import jr.dungeon.events.DungeonEventListener;
import jr.utils.MultiLineNoPrefixToStringStyle;
import jr.utils.Serialisable;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A state for stateful AI. A state is a certain action with intrinsic information that occurs over a specified
 * duration of turns. The state will return to the AI's default state when complete.
 *
 * If the state duration is 0, it will run indefinitely, until it manually assigns another state to the AI.
 *
 * This AI system may be replaced soon.
 */
@Getter
public class AIState implements Serialisable, DungeonEventListener {
	private StatefulAI ai;
	
	private int duration = 0;
	private int turnsTaken = 0;
	
	/**
	 * @param ai The {@link StatefulAI} that hosts this state.
	 * @param duration How many turns the state should run for. 0 for indefinite.
	 */
	public AIState(StatefulAI ai, int duration) {
		this.ai = ai;
		this.duration = duration;
	}
	
	public StatefulAI getAI() {
		return ai;
	}
	
	/**
	 * Called every turn - this is the state's chance to assign an action, or assign another state.
	 */
	public void update() {
		turnsTaken = Math.max(0, turnsTaken - 1);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		
		obj.put("duration", duration);
		obj.put("turnsTaken", turnsTaken);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		turnsTaken = obj.optInt("turnsTaken");
	}
	
	/**
	 * Instantiates and unserialises an AIState from serialised JSON.
	 *
	 * @param serialisedState The previously serialised JSONObject containing the AIState information.
	 * @param ai The {@link StatefulAI} that hosts this state.
	 *
	 * @return A fully unserialised AIState instance.
	 */
	@SuppressWarnings("unchecked")
	public static AIState createFromJSON(JSONObject serialisedState, StatefulAI ai) {
		String stateClassName = serialisedState.getString("class");
		
		try {
			Class<? extends AIState> stateClass = (Class<? extends AIState>) Class.forName(stateClassName);
			Constructor<? extends AIState> stateConstructor = stateClass.getConstructor(StatefulAI.class, int.class);
			
			int duration = serialisedState.optInt("duration");
			
			AIState state = stateConstructor.newInstance(ai, duration);
			state.unserialise(serialisedState);
			return state;
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown AIState class {}", stateClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("AIState class {} has no unserialisation constructor", stateClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading AIState class {}", stateClassName);
			JRogue.getLogger().error(e);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append("duration", duration)
			.append("turnsTaken", turnsTaken)
			.toString();
	}
}
