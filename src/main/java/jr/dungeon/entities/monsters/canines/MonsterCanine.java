package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.actions.EntityAction;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityKickedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.GhoulAI;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEventHandler;
import jr.utils.RandomUtils;

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
	
	@DungeonEventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		getDungeon().logRandom("It whimpers.", "It whines.", "It cries.", "It yelps.");
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onKick(EntityKickedEvent e) {
		if (e.isKickerPlayer()) {
			getDungeon().You("kick the %s!", getName(e.getKicker(), false));
		}
		
		int dodgeChance = 5;
		
		if (e.isKickerPlayer()) {
			Player player = (Player) e.getKicker();
			int agility = player.getAttributes().getAttribute(Attribute.AGILITY);
			dodgeChance = (int) Math.ceil(agility / 3);
		}
		
		if (RandomUtils.roll(1, dodgeChance) == 1) {
			if (e.isKickerPlayer()) {
				getDungeon().orangeThe("%s dodges your kick!", getName(e.getKicker(), false));
			}
			
			return;
		}
				
		int damageChance = 2;
		
		if (e.isKickerPlayer()) {
			Player player = (Player) e.getKicker();
			int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
			damageChance = (int) Math.ceil(strength / 6) + 1;
		}
		
		if (RandomUtils.roll(1, damageChance) == 1) {
			damage(DamageSource.PLAYER_KICK, 1, e.getKicker());
		}
		
		if (isAlive()) {
			if (RandomUtils.roll(1, 5) == 1) {
				if (e.isKickerPlayer()) {
					getDungeon().orangeThe("%s bites your foot!", getName(e.getKicker(), false));
				}
				
				if (RandomUtils.roll(1, 4) == 1) {
					if (e.isKickerPlayer()) {
						getDungeon().redThe("bite was pretty deep!");
					}
					
					e.getKicker().damage(DamageSource.KICK_REVENGE, 1, e.getKicker());
					e.getKicker().addStatusEffect(new InjuredFoot(getDungeon(), e.getKicker(), RandomUtils.roll(3, 6)));
				}
			} else if (RandomUtils.roll(1, 5) == 1) {
				if (e.isKickerPlayer()) {
					getDungeon().orangeThe("%s yanks your leg!", getName(e.getKicker(), false));
				}
				
				if (RandomUtils.roll(1, 4) == 1) {
					if (e.isKickerPlayer()) {
						getDungeon().log("[RED]It strains your leg!");
					}
					
					e.getKicker().damage(DamageSource.KICK_REVENGE, 1, e.getKicker());
					e.getKicker().addStatusEffect(new StrainedLeg(RandomUtils.roll(3, 6)));
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
