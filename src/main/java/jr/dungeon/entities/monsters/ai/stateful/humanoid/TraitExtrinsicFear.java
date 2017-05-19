package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.events.EventHandler;
import jr.utils.RandomUtils;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

@Getter
public class TraitExtrinsicFear extends AITrait<StatefulAI> {
	private float fear;
	
	public TraitExtrinsicFear(StatefulAI ai) {
		super(ai);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("extrinsicFear", fear);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		fear = obj.getInt("extrinsicFear");
	}
	
	@Override
	public void update() {
		if (RandomUtils.randomFloat() <= 0.05f) {
			fear = 0;
		}
	}
	
	@EventHandler
	private void onMonsterDamaged(EntityDamagedEvent e) {
		if (e.getVictim() != getMonster()) {
			return;
		}
		
		// TODO: ensure this is balanced
		
		if (
			getMonster().getHealth() < getMonster().getMaxHealth() / 2.5f &&
			e.getAttacker() instanceof EntityLiving &&
			((EntityLiving) e.getAttacker()).getHealth() > ((EntityLiving) e.getAttacker()).getMaxHealth() / 1.5f
		) {
			fear += (1 - getMonster().getHealth() / getMonster().getMaxHealth()) * RandomUtils.randomFloat();
		}
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("fear", fear);
	}
}
