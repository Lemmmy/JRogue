package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Appearance;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
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
		if (requiresCapitalisation) {
			return "Jackal";
		} else {
			return "jackal";
		}
	}

	@Override
	public Appearance getAppearance() {
		return Appearance.APPEARANCE_JACKAL;
	}

	@Override
	protected void onKick(Entity kicker) {
		getDungeon().You("kick the %s!", getName(false));

		if (Utils.roll(1, 3) == 1) { // TODO: Make this dependent on player strength?
			damage(DamageSource.PLAYER_KICK, 1);
		}
	}
}
