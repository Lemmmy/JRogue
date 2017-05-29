package jr.dungeon.items.weapons;

import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.projectiles.ItemProjectile;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;

public class ItemStaff extends ItemWeaponMelee {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.staff.clone();
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
	public DamageType getMeleeDamageSourceType() {
		return DamageType.STAFF_BASH;
	}
	
	@Override
	public Verb getMeleeAttackVerb() {
		return Lexicon.bash.clone();
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
