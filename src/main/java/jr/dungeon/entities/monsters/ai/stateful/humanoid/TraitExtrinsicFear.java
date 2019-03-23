package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.events.EventHandler;
import jr.dungeon.serialisation.Registered;
import jr.utils.RandomUtils;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Registered(id="aiTraitHumanoidExtrinsicFear")
public class TraitExtrinsicFear extends AITrait<StatefulAI> {
	@Expose private float fear;
	
	public TraitExtrinsicFear(StatefulAI ai) {
		super(ai);
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
			fear += (1 - (float) getMonster().getHealth() / (float) getMonster().getMaxHealth()) * RandomUtils.randomFloat();
		}
	}
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("fear", fear);
	}
}
