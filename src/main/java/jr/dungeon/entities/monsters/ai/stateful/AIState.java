package jr.dungeon.entities.monsters.ai.stateful;

import jr.utils.MultiLineNoPrefixToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;
import jr.JRogue;
import jr.utils.Serialisable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AIState implements Serialisable {
	private StatefulAI ai;
	
	private int duration = 0;
	private int turnsTaken = 0;
	
	public AIState(StatefulAI ai, int duration) {
		this.ai = ai;
		this.duration = duration;
	}
	
	public void update() {
		turnsTaken = Math.max(0, turnsTaken - 1);
	}
	
	public StatefulAI getAI() {
		return ai;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getTurnsTaken() {
		return turnsTaken;
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
