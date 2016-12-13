package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

public class EntityFountain extends Entity implements PassiveSoundEmitter {
	public EntityFountain(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	@Override
	public String getName(boolean requiresCapitalisation) {
		return "Fountain";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_FOUNTAIN;
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {

	}

	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
	}

	@Override
	public boolean canBeWalkedOn() {
		return true;
	}

	@Override
	public float getSoundProbability() {
		return 0.25f;
	}

	@Override
	public String[] getSounds() {
		return new String[] {
			"You hear a light splashing sound.",
			"You hear a light splishing sound.",
			"You hear a light pattering sound.",
			"You hear the splashing of water.",
			"You hear the trickling of water.",
			"You hear the rushing of water.",
			"You hear bubbling water.",
			"You hear gushing water.",
			"You hear water falling on coins.",
			"You hear water pattering on coins.",
		};
	}
}
