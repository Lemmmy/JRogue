package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;

import java.util.List;

public class MonsterHound extends MonsterCanine {
	public MonsterHound(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);

		if (getAI() instanceof GhoulAI) {
			((GhoulAI) getAI()).setAttackProbability(0.95f);
		}
	}

	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED;
	}

	@Override
	public Size getSize() {
		return Size.SMALL;
	}

	@Override
	public int getWeight() {
		return 300;
	}

	@Override
	public int getNutrition() {
		return 200;
	}

	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		return null;
	}

	@Override
	public int getVisibilityRange() {
		return 15;
	}
	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Hound": "hound";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_HOUND;
	}
}
