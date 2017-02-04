package jr.dungeon.entities.monsters.ai.stateful;

import jr.JRogue;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.events.DungeonEventListener;
import jr.utils.Serialisable;
import lombok.Getter;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Getter
public abstract class AITrait implements Serialisable, DungeonEventListener {
	private Monster monster;
	private StatefulAI ai;
	
	public AITrait(StatefulAI ai) {
		this.ai = ai;
		this.monster = ai.getMonster();
	}
	
	public abstract void update();
	
	public StatefulAI getAI() {
		return ai;
	}
	
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
