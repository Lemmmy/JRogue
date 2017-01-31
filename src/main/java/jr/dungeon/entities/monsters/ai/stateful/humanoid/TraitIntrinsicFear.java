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
		obj.put("intrinsicFear", fear);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		fear = obj.getInt("intrinsicFear");
	}
	
	@Override
	public void update() {
		// TODO: ensure this is balanced
		
		// if the current target's armour class is 5 lower than our own,
		// extrinsic fear is 0.5
		
		if (
			getAI().getCurrentTarget() != null &&
			getMonster().getArmourClass() - getAI().getCurrentTarget().getArmourClass() >= 5 &&
			RandomUtils.randomFloat() < 0.25f
		) {
			fear = 0.5f;
		}
	}
}
