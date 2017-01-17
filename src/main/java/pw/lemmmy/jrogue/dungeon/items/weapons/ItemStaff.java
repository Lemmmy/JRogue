package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemProjectile;

public class ItemStaff extends ItemWeaponMelee {
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
			
		if (requiresCapitalisation) {
			s += plural ? "Staves" : "Staff";
		} else {
			s += plural ? "staves" : "staff";
		}
		
		return s;
	}
	
	@Override
	public float getWeight() {
		return 50;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_STAFF;
	}
	
	@Override
	public void zap(EntityLiving attacker, EntityLiving victim, int dx, int dy) {}
	
	@Override
	public boolean fire(EntityLiving attacker, ItemProjectile projectile, int dx, int dy) {
		return false;
	}
	
	@Override
	public boolean isMelee() {
		return true;
	}
	
	@Override
	public boolean isRanged() {
		return false;
	}
	
	@Override
	public boolean isMagic() {
		return false;
	}
	
	@Override
	protected DamageSource getMeleeDamageSource() {
		return DamageSource.STAFF_BASH;
	}
	
	@Override
	public void onHit(EntityLiving attacker, EntityLiving victim) {
		hitLog("You bash the %s!", "The %s bashes you!", "The %s bashes the %s!", attacker, victim);
	}
	
	@Override
	public int getSmallDamage() {
		return 4;
	}
	
	@Override
	public int getLargeDamage() {
		return 3;
	}
	
	@Override
	public int getToHitBonus() {
		return 0;
	}
	
	@Override
	public Skill getSkill() {
		return Skill.SKILL_STAFF;
	}
	
	@Override
	public boolean isTwoHanded() {
		return true;
	}
}
