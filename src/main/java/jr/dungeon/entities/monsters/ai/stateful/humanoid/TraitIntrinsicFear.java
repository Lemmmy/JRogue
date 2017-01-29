package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.utils.RandomUtils;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class TraitIntrinsicFear extends AITrait {
	private float fear;
	
	public TraitIntrinsicFear(StatefulAI ai) {
		super(ai);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("fear", fear);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		fear = obj.getInt("fear");
	}
	
	@Override
	public void update() {
		if (RandomUtils.randomFloat() < 0.04f) {
			fear = 0;
		}
		
		if (getMonster().getHealth() < getMonster().getMaxHealth() / 2) {
			
		}
	}
}
