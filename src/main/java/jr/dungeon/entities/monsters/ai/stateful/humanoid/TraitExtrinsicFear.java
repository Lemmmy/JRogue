package jr.dungeon.entities.monsters.ai.stateful.humanoid;

import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.events.DungeonEventHandler;
import jr.utils.RandomUtils;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class TraitExtrinsicFear extends AITrait {
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
		if (RandomUtils.randomFloat() < 0.04f) {
			fear = 0;
		}
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onMonsterDamaged(EntityDamagedEvent e) {
		// TODO: ensure this is balanced
		
		// when the monster is attacked,
		//  if their health is less than their maxHealth / 2.5,
		//  and the attacker's health is greater than their maxHealth / 1.5,
		//  and a d2 has a positive roll,
		//  increase extrinsic fear by monster.maxHealth * rand(0, 1)
		
		if (
			getMonster().getHealth() < getMonster().getMaxHealth() / 2.5f &&
			e.getAttacker().getHealth() > e.getAttacker().getMaxHealth() / 1.5f &&
			RandomUtils.rollD2()
		) {
			fear += getMonster().getMaxHealth() * RandomUtils.randomFloat();
		}
	}
}
