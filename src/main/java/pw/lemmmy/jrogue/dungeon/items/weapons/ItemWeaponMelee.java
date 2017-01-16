package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.utils.RandomUtils;

public abstract class ItemWeaponMelee extends ItemWeapon {
	@Override
	public void hit(LivingEntity attacker, LivingEntity victim) {
		int baseDamage = calculateDamage(attacker, victim);
		int damage = baseDamage > 0 ? RandomUtils.roll(baseDamage) : baseDamage;
		
		attacker.setAction(new ActionMelee(
			victim,
			getMeleeDamageSource(),
			damage,
			new EntityAction.ActionCallback() {
				@Override
				public void beforeRun(Entity entity) {
					onHit(attacker, victim);
				}
			}
		));
	}
	
	protected int calculateDamage(LivingEntity attacker, LivingEntity victim) {
		int damage = victim.getSize() == LivingEntity.Size.SMALL ? getSmallDamage() : getLargeDamage();
		
		if (attacker instanceof Player) {
			Player player = (Player) attacker;
			
			SkillLevel skillLevel = player.getSkillLevel(getSkill());
			int skillModifier = skillLevel.getMeleeWeaponDamage();
			
			damage += skillModifier;
		}
		
		return damage;
	}
	
	protected abstract DamageSource getMeleeDamageSource();
	
	public abstract void onHit(LivingEntity attacker, LivingEntity victim);
	
	public abstract int getSmallDamage();
	
	public abstract int getLargeDamage();
	
	public void hitLog(String attackerString,
					   String victimString,
					   String neitherString,
					   LivingEntity attacker,
					   LivingEntity victim) {
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
