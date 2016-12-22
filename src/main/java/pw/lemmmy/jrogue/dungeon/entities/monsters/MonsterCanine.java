package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
import pw.lemmmy.jrogue.dungeon.items.ItemCorpse;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.utils.Utils;

public abstract class MonsterCanine extends Monster {
	public MonsterCanine(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);

		setAI(new GhoulAI(this));
	}

	@Override
	public boolean isHostile() {
		return true;
	}

	@Override
	public float getCorpseChance() {
		return 0.5f;
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage, Entity attacker, boolean isPlayer) {
		getDungeon().logRandom("It whimpers.", "It whines.", "It cries.", "It yelps.");
	}

	@Override
	protected void onDie(DamageSource damageSource) {
		getDungeon().You("kill the %s!", getName(false));
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
			setAction(new ActionMelee(
				getDungeon(),
				this,
				getDungeon().getPlayer(),
				DamageSource.CANINE_BITE,
				1,
				new EntityAction.ActionCallback() {
					@Override
					public void onComplete() {
						getDungeon().orangeThe("%s bites you!", getName(false));
					}
				}
			));
		}
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {
		getDungeon().You("kick the %s!", getName(false));

		if (Utils
			.roll(1, 5) == 1) { // TODO: If a player has a higher agility or speed, the monster is less likely to dodge
			getDungeon().orangeThe("%s dodges your kick!", getName(false));

			return;
		}

		if (Utils.roll(1, 2) == 1) { // TODO: Make this dependent on player strength and martial arts skill
			damage(DamageSource.PLAYER_KICK, 1, kicker, isPlayer);
		}

		if (isAlive()) {
			if (Utils.roll(1, 5) == 1) {
				getDungeon().orangeThe("%s bites your foot!", getName(false));

				if (Utils.roll(1, 4) == 1) {
					getDungeon().redThe("The bite was pretty deep!");

					kicker.damage(DamageSource.KICK_REVENGE, 1, kicker, isPlayer);
					kicker.addStatusEffect(new InjuredFoot(getDungeon(), kicker, Utils.roll(3, 6)));
				}
			} else if (Utils.roll(1, 5) == 1) {
				getDungeon().orangeThe("%s yanks your leg!", getName(false));

				if (Utils.roll(1, 4) == 1) {
					getDungeon().log("[RED]It strains your leg!");

					kicker.damage(DamageSource.KICK_REVENGE, 1, kicker, isPlayer);
					kicker.addStatusEffect(new StrainedLeg(getDungeon(), kicker, Utils.roll(3, 6)));
				}
			}
		}
	}
}
