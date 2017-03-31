package jr.dungeon.items.weapons;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.skills.SkillLevel;
import jr.utils.RandomUtils;

public abstract class ItemWeaponMelee extends ItemWeapon {
	@Override
	public void hit(EntityLiving attacker, EntityLiving victim) {
		int baseDamage = calculateDamage(attacker, victim);
		int damage = baseDamage > 0 ? RandomUtils.roll(baseDamage) : baseDamage;
		
		attacker.setAction(new ActionMelee(
			victim,
			generateMeleeDamageSource(attacker),
			damage,
			new Action.ActionCallback() {
				@Override
				public void beforeRun(Entity entity) {
					onHit(attacker, victim);
				}
			}
		));
	}
	
	protected int calculateDamage(EntityLiving attacker, EntityLiving victim) {
		int damage = victim.getSize() == EntityLiving.Size.SMALL ? getSmallDamage() : getLargeDamage();
		
		if (attacker instanceof Player) {
			Player player = (Player) attacker;
			
			SkillLevel skillLevel = player.getSkillLevel(getSkill());
			int skillModifier = skillLevel.getMeleeWeaponDamage();
			
			damage += skillModifier;
		}
		
		return damage;
	}
	
	public abstract DamageSourceType getMeleeDamageSourceType();
	
	public DamageSource generateMeleeDamageSource(EntityLiving attacker) {
		return new DamageSource(attacker, this, getMeleeDamageSourceType());
	}
	
	public abstract void onHit(EntityLiving attacker, EntityLiving victim);
	
	public abstract int getSmallDamage();
	
	public abstract int getLargeDamage();
	
	public void hitLog(String attackerString,
					   String victimString,
					   String neitherString,
					   EntityLiving attacker,
					   EntityLiving victim) {
		if (victim.getHealth() <= 0) { return; }
		
		if (attacker instanceof Player) {
			attacker.getDungeon().log(attackerString, victim.getName(attacker, false));
		} else if (victim instanceof Player) {
			victim.getDungeon().log(victimString, attacker.getName(victim, false));
		} else {
			attacker.getDungeon().log(
				neitherString,
				attacker.getName(victim.getDungeon().getPlayer(), false),
				victim.getName(victim.getDungeon().getPlayer(), false)
			);
		}
	}
}
