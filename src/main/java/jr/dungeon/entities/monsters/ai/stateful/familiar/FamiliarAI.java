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
		
		removeTrait(TraitIntrinsicFear.class);
		removeTrait(TraitExtrinsicFear.class);
		removeTrait(TraitBewareTarget.class);
		
		addTrait(new TraitFollowPartner(this));
		addTrait(new TraitDefendPartner(this));
		addTrait(new TraitAvoidCursed(this));
		addTrait(new TraitHunger(this));
	}
}
