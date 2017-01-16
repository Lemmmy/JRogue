package pw.lemmmy.jrogue.dungeon.entities.monsters.canines;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.effects.InjuredFoot;
import pw.lemmmy.jrogue.dungeon.entities.effects.StrainedLeg;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.utils.RandomUtils;

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
	public int getBaseArmourClass() {
		return 7;
	}
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		getDungeon().logRandom("It whimpers.", "It whines.", "It cries.", "It yelps.");
	}
	
	@Override
	protected void onDie(DamageSource damageSource, int damage, LivingEntity attacker, boolean isPlayer) {
		if (isPlayer) {
			getDungeon().You("kill the %s!", getName(attacker, false));
		}
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {
		if (isPlayer) {
			getDungeon().You("kick the %s!", getName(kicker, false));
		}
		
		int dodgeChance = 5;
		
		if (isPlayer) {
			Player player = (Player) kicker;
			int agility = player.getAttributes().getAttribute(Attribute.AGILITY);
			dodgeChance = (int) Math.ceil(agility / 3);
		}
		
		if (RandomUtils.roll(1, dodgeChance) == 1) {
			if (isPlayer) {
				getDungeon().orangeThe("%s dodges your kick!", getName(kicker, false));
			}
			
			return;
		}
				
		int damageChance = 2;
		
		if (isPlayer) {
			Player player = (Player) kicker;
			int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
			damageChance = (int) Math.ceil(strength / 6) + 1;
		}
		
		if (RandomUtils.roll(1, damageChance) == 1) {
			damage(DamageSource.PLAYER_KICK, 1, kicker, isPlayer);
		}
		
		if (isAlive()) {
			if (RandomUtils.roll(1, 5) == 1) {
				if (isPlayer) {
					getDungeon().orangeThe("%s bites your foot!", getName(kicker, false));
				}
				
				if (RandomUtils.roll(1, 4) == 1) {
					if (isPlayer) {
						getDungeon().redThe("bite was pretty deep!");
					}
					
					kicker.damage(DamageSource.KICK_REVENGE, 1, kicker, isPlayer);
					kicker.addStatusEffect(new InjuredFoot(getDungeon(), kicker, RandomUtils.roll(3, 6)));
				}
			} else if (RandomUtils.roll(1, 5) == 1) {
				if (isPlayer) {
					getDungeon().orangeThe("%s yanks your leg!", getName(kicker, false));
				}
				
				if (RandomUtils.roll(1, 4) == 1) {
					if (isPlayer) {
						getDungeon().log("[RED]It strains your leg!");
					}
					
					kicker.damage(DamageSource.KICK_REVENGE, 1, kicker, isPlayer);
					kicker.addStatusEffect(new StrainedLeg(RandomUtils.roll(3, 6)));
				}
			}
		}
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
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			DamageSource.CANINE_BITE,
			1,
			(EntityAction.CompleteCallback) e -> getDungeon().orangeThe("%s bites you!", getName(getDungeon().getPlayer(), false))
		));
	}
}
