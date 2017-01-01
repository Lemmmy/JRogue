package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.projectiles.ItemProjectile;

public class ItemStaff extends ItemWeaponMelee {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		if (requiresCapitalisation) {
			return plural ? "Staves" : "Staff";
		} else {
			return plural ? "staves" : "staff";
		}
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
	public void zap(LivingEntity attacker, LivingEntity victim, int dx, int dy) {}
	
	@Override
	public void fire(LivingEntity attacker, ItemProjectile projectile, int dx, int dy) {}
	
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
	public void onHit(LivingEntity attacker, LivingEntity victim) {
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
