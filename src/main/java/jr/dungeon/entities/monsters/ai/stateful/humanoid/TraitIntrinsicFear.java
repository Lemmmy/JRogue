package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.utils.MultiLineNoPrefixToStringStyle;
import jr.utils.Utils;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

@Getter
public class TraitIntrinsicFear extends AITrait<StatefulAI> {
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
		
		if (
			getAI().getCurrentTarget() != null &&
			Utils.chebyshevDistance(getAI().getCurrentTarget().getPosition(), getMonster().getPosition()) < 5
		) {
			fear = (getMonster().getArmourClass() - getAI().getCurrentTarget().getArmourClass()) / 10;
			fear = Math.max(0, Math.min(1, fear));
		}
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, MultiLineNoPrefixToStringStyle.STYLE)
			.append("fear", fear)
			.toString();
	}
}
