package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
import pw.lemmmy.jrogue.dungeon.items.ItemCorpse;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

public class MonsterJackal extends Monster {
	public MonsterJackal(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);

		setAI(new GhoulAI(this));
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage) {
		getDungeon().logRandom("It whimpers.", "It whines.", "It cries.", "It yelps.");
	}

	@Override
	protected void onDie(DamageSource damageSource) {
		getDungeon().You("kill the %s!", getName(false));

		//if (Utils.roll(1, 2) == 1) {
		drop(new ItemStack(new ItemCorpse(this)));
		//}
	}

	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED;
	}

	@Override
	public int getVisibilityRange() {
		return 15;
	}

	@Override
	public boolean canMoveDiagonally() {
		return true;
	}

	@Override
	public boolean canMeleeAttack() {
		return true;
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
	public void meleeAttackPlayer() {
		if (Utils.roll(1, 2) == 1) {
			getDungeon().getPlayer().damage(DamageSource.CANINE_BITE, 1);
			getDungeon().The("%s bites you!", getName(false));
		}
	}

	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Jackal": "jackal";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_JACKAL;
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		if (Utils.roll(1, 5) == 1) { // TODO: If a player has a higher agility, the monster is less likely to dodge
			getDungeon().The("%s dodges your kick!", getName(false));

			return;
		}

		getDungeon().You("kick the %s!", getName(false));

		if (Utils.roll(1, 3) == 1) { // TODO: Make this dependent on player strength?
			damage(DamageSource.PLAYER_KICK, 5);
		}

		if (isAlive()) {
			if (Utils.roll(1, 5) == 1) {
				getDungeon().The("%s bites your foot!", getName(false));

				if (Utils.roll(1, 3) == 1) {
					getDungeon().log("The bite was pretty deep!");

					kicker.damage(DamageSource.KICK_REVENGE, 1);
					kicker.addStatusEffect(new InjuredFoot(getDungeon(), kicker, Utils.roll(3, 6)));
				}
			} else if (Utils.roll(1, 5) == 1) {
				getDungeon().The("%s yanks your leg!", getName(false));

				if (Utils.roll(1, 3) == 1) {
					getDungeon().log("It strains your leg!");

					kicker.damage(DamageSource.KICK_REVENGE, 1);
					kicker.addStatusEffect(new StrainedLeg(getDungeon(), kicker, Utils.roll(3, 6)));
				}
			}
		}
	}
}
