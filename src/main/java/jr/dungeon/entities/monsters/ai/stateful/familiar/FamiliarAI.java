package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;

public class FamiliarAI extends StatefulAI {
	public FamiliarAI(Monster monster) {
		super(monster);
		
		getPersistence().put("lurkRadius", 4);
		getPersistence().put("lurkTarget", monster.getDungeon().getPlayer().getUUID().toString());
		
		addTrait(new TraitFollowOwner(this));
	}
}
