package jr.dungeon.entities.monsters.ai.stateful;

import jr.JRogue;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.events.DungeonEventListener;
import jr.utils.Serialisable;
import lombok.Getter;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
 */
@Getter
public abstract class AITrait implements Serialisable, DungeonEventListener {
	private Monster monster;
	private StatefulAI ai;
	
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link StatefulAI} behaves.
	 *
	 * @param ai The {@link StatefulAI} that hosts this trait.
	 */
	public AITrait(StatefulAI ai) {
		this.ai = ai;
		this.monster = ai.getMonster();
	}
	
	/**
	 * Called every turn when the AI's monster gets a turn to move.
	 */
	public abstract void update();
	
	public StatefulAI getAI() {
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
			JRogue.getLogger().error("Error loading AITrait class {}", traitClassName);
			JRogue.getLogger().error(e);
		}
		
		return null;
	}
}
