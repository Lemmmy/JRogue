package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;

public abstract class ItemWeaponMelee extends ItemWeapon {
	@Override
	public void hit(LivingEntity attacker, LivingEntity victim) {

	}

	public void hitLog(String attackerString, String victimString, String neitherString, LivingEntity attacker, LivingEntity victim) {
		if (attacker instanceof Player) {
			attacker.getDungeon().log(attackerString, victim.getName(false));
		} else if (victim instanceof Player) {
			victim.getDungeon().log(victimString, attacker.getName(false));
		} else {
			attacker.getDungeon().log(neitherString, attacker.getName(false), victim.getName(false));
		}
	}

	public abstract void onHit(LivingEntity attacker, LivingEntity victim);

	public abstract Skill getSkill();

	public abstract int getSmallDamage();
	public abstract int getLargeDamage();
}
