package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.utils.Utils;

public abstract class ItemWeaponMelee extends ItemWeapon {
	@Override
	public void hit(LivingEntity attacker, LivingEntity victim) {
		int baseDamage = calculateDamage(attacker, victim);
		int damage = Utils.roll(baseDamage);

		attacker.setAction(new ActionMelee(
			attacker.getDungeon(),
			attacker,
			victim,
			getMeleeDamageSource(),
			damage,
			new EntityAction.ActionCallback() {
				@Override
				public void beforeRun() {
					onHit(attacker, victim);
				}
			}
		));
	}

	public void hitLog(String attackerString, String victimString, String neitherString, LivingEntity attacker, LivingEntity victim) {
		if (victim.getHealth() <= 0) return;

		if (attacker instanceof Player) {
			attacker.getDungeon().log(attackerString, victim.getName(false));
		} else if (victim instanceof Player) {
			victim.getDungeon().log(victimString, attacker.getName(false));
		} else {
			attacker.getDungeon().log(neitherString, attacker.getName(false), victim.getName(false));
		}
	}

	protected int calculateDamage(LivingEntity attacker, LivingEntity victim) {
		int damage = victim.getSize() == LivingEntity.Size.SMALL ? getSmallDamage() : getLargeDamage();

		if (attacker instanceof Player) {
			Player player = (Player) attacker;

			SkillLevel skillLevel = player.getSkillLevel(getSkill());
			int skillModifier = skillLevel.getMeleeWeaponDamage();

			damage += skillModifier;
		}

		return Math.max(0, damage);
	}

	public abstract void onHit(LivingEntity attacker, LivingEntity victim);

	public abstract Skill getSkill();

	public abstract int getSmallDamage();
	public abstract int getLargeDamage();

	protected abstract DamageSource getMeleeDamageSource();
}
