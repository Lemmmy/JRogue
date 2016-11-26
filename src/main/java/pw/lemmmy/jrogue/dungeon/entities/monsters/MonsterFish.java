package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.FishAI;
import pw.lemmmy.jrogue.dungeon.items.ItemCorpse;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.List;

public class MonsterFish extends Monster {
	private FishColour colour;

	public MonsterFish(Dungeon dungeon, Level level, int x, int y, FishColour colour) {
		super(dungeon, level, x, y, 1);

		this.colour = colour;

		setAI(new FishAI(this));
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {
		getDungeon().logRandom("Bloop.", "Glug.", "Splash!", "Sploosh!");
	}

	@Override
	protected void onDie(DamageSource damageSource) {
		getDungeon().You("kill the %s!", getName(false));

		if (Utils.roll(1, 3) == 1) {
			drop(new ItemStack(new ItemCorpse(this)));
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
		return 50;
	}

	@Override
	public int getNutrition() {
		return 50;
	}

	@Override
	public List<StatusEffect> getCorpseEffects() {
		return null;
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
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Fish": "fish";
	}

	@Override
	public EntityAppearance getAppearance() {
		switch (colour) {
			case RED:
				return EntityAppearance.APPEARANCE_FISH_RED;
			case ORANGE:
				return EntityAppearance.APPEARANCE_FISH_ORANGE;
			case YELLOW:
				return EntityAppearance.APPEARANCE_FISH_YELLOW;
			case GREEN:
				return EntityAppearance.APPEARANCE_FISH_GREEN;
			case BLUE:
				return EntityAppearance.APPEARANCE_FISH_BLUE;
			case PURPLE:
				return EntityAppearance.APPEARANCE_FISH_PURPLE;
			default:
				return EntityAppearance.APPEARANCE_FISH_BLUE;
		}
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("kick the %s!", getName(false));
	}

	public enum FishColour {
		RED, YELLOW, ORANGE, GREEN, BLUE, PURPLE
	}
}
