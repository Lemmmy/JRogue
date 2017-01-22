package jr.dungeon.entities.monsters.canines;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.utils.RandomUtils;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.actions.EntityAction;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.GhoulAI;

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
	protected void onDamage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {
		getDungeon().logRandom("It whimpers.", "It whines.", "It cries.", "It yelps.");
	}
	
	@Override
	protected void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
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
	public void meleeAttack(EntityLiving victim) {
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			DamageSource.CANINE_BITE,
			1,
			(EntityAction.CompleteCallback) e -> getDungeon().orangeThe("%s bites you!", getName(getDungeon().getPlayer(), false))
		));
	}
}
