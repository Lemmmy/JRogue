package jr.dungeon.items.weapons;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.skills.SkillLevel;
import jr.language.LanguageUtils;
import jr.language.Verb;
import jr.language.transformers.Capitalise;
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
	
	public abstract DamageType getMeleeDamageSourceType();
	
	public DamageSource generateMeleeDamageSource(EntityLiving attacker) {
		return new DamageSource(attacker, this, getMeleeDamageSourceType());
	}
	
	public abstract int getSmallDamage();
	
	public abstract int getLargeDamage();
	
	public abstract Verb getMeleeAttackVerb();
	
	public void onHit(EntityLiving attacker, EntityLiving victim) {
		hitLog(attacker, victim);
	}
	
	public void hitLog(EntityLiving attacker, EntityLiving victim) {
		if (victim.getHealth() <= 0) { return; }
		
		victim.getDungeon().log(
			"%s %s %s!",
			LanguageUtils.subject(attacker).build(Capitalise.first),
			LanguageUtils.autoTense(getMeleeAttackVerb(), attacker),
			LanguageUtils.object(victim)
		);
	}
}
