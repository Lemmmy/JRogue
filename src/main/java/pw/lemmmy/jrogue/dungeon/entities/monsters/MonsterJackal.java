package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
import pw.lemmmy.jrogue.dungeon.items.ItemCorpse;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.List;

public class MonsterJackal extends MonsterCanine {
	public MonsterJackal(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED;
	}

	@Override
	public Size getSize() {
		return LivingEntity.Size.SMALL;
	}

	@Override
	public int getWeight() {
		return 300;
	}

	@Override
	public int getNutrition() {
		return 250;
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
		return requiresCapitalisation ? "Jackal": "jackal";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_JACKAL;
	}
}
