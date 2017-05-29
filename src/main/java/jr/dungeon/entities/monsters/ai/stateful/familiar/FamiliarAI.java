package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.TraitBewareTarget;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitExtrinsicFear;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.TraitIntrinsicFear;

public class FamiliarAI extends StatefulAI {
	public FamiliarAI(Monster monster) {
		super(monster);
		
		setPathfinder(new FamiliarPathfinder());
		
		setShouldTargetPlayer(false);
		
		getPersistence().putOnce("lurkRadius", 3);
		
		if (monster.getDungeon().getPlayer() != null) {
			getPersistence().putOnce("lurkTarget", monster.getDungeon().getPlayer().getUUID().toString());
		}
		
		removeTrait(TraitIntrinsicFear.class);
		removeTrait(TraitExtrinsicFear.class);
		removeTrait(TraitBewareTarget.class);
		
		addTrait(new TraitFollowOwner(this));
		addTrait(new TraitDefendOwner(this));
		addTrait(new TraitAvoidCursed(this));
		addTrait(new TraitHunger(this));
	}
}
