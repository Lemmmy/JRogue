package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitExtrinsicFear;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitIntrinsicFear;

public class FamiliarAI extends StatefulAI {
	public FamiliarAI(Monster monster) {
		super(monster);
		
		setShouldTargetPlayer(false);
		
		getPersistence().put("lurkRadius", 4);
		getPersistence().put("lurkTarget", monster.getDungeon().getPlayer().getUUID().toString());
		
		removeTrait(TraitIntrinsicFear.class);
		removeTrait(TraitExtrinsicFear.class);
		
		addTrait(new TraitFollowOwner(this));
		addTrait(new TraitDefendOwner(this));
	}
}
