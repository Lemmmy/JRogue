package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.Poison;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.FishAI;

import java.util.ArrayList;
import java.util.List;

public class MonsterPufferfish extends Monster {
	public MonsterPufferfish(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);

		setAI(new FishAI(this));
	}

	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Pufferfish" : "pufferfish";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_PUFFERFISH;
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
	public boolean isHostile() {
		return false;
	}

	@Override
	public int getWeight() {
		return 50;
	}

	@Override
	public int getNutrition() {
		return 50;
	}

	@Override
	public float getCorpseChance() {
		return 0.15f;
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {
		getDungeon().logRandom("Bloop.", "Glug.", "Splash!", "Sploosh!");
	}

	@Override
	protected void onDie(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {
		getDungeon().You("kill the %s!", getName(false));
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("kick the %s!", getName(false));
	}

	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		List<StatusEffect> effects = new ArrayList<>();

		effects.add(new Poison());

		return effects;
	}

	@Override
	public int getVisibilityRange() {
		return 2;
	}

	@Override
	public boolean canMoveDiagonally() {
		return true;
	}

	@Override
	public boolean canMeleeAttack() {
		return false;
	}

	@Override
	public boolean canRangedAttack() {
		return false;
	}

	@Override
	public boolean canMagicAttack() {
		return false;
	}

	@Override
	public int getExperienceRewarded() {
		return 0;
	}
}
