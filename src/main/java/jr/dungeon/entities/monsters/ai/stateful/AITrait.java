package jr.dungeon.entities.monsters.ai.stateful;

import jr.JRogue;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.events.EventListener;
import jr.utils.DebugToStringStyle;
import jr.utils.Serialisable;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
 */
@Getter
public abstract class AITrait<T extends StatefulAI> implements Serialisable, EventListener {
	private Monster monster;
	private T ai;
	
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
	 *
	 * @param ai The {@link StatefulAI} that hosts this trait.
	 */
	public AITrait(T ai) {
		this.ai = ai;
		this.monster = ai.getMonster();
	}
	
	/**
	 * Called every turn when the AI's monster gets a turn to move.
	 */
	public abstract void update();
	
	public T getAI() {
		return ai;
	}
	
	/**
	 * Instantiates and unserialises an AITrait from serialised JSON.
	 *
	 * @param traitClassName The class name to instantiate. Must extend AITrait.
	 * @param serialisedTrait The previously serialised JSONObject containing the AITrait information.
	 * @param ai The {@link StatefulAI} that hosts this trait.
	 *
	 * @return A fully unserialised AITrait instance.
	 */
	@SuppressWarnings("unchecked")
	public static AITrait createFromJSON(String traitClassName, JSONObject serialisedTrait, StatefulAI ai) {
		try {
			Class<? extends AITrait> traitClass = (Class<? extends AITrait>) Class.forName(traitClassName);
			Constructor<? extends AITrait> traitConstructor = traitClass.getConstructor(StatefulAI.class);
			
			AITrait trait = traitConstructor.newInstance(ai);
			trait.unserialise(serialisedTrait);
			return trait;
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown AITrait class {}", traitClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("AITrait class {} has no unserialisation constructor", traitClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading AITrait class {}", traitClassName, e);
		}
		
		return null;
	}
	
	public int getPriority() {
		return 0;
	}
	
	@Override
	public Set<Object> getListenerSelves() {
		val selves = new HashSet<>();
		selves.add(this);
		selves.add(ai);
		selves.add(monster);
		return selves;
	}
	
	@Override
	public String toString() {
		return toStringBuilder().build();
	}
	
	public ToStringBuilder toStringBuilder() {
		return new ToStringBuilder(this, DebugToStringStyle.STYLE);
	}
}
