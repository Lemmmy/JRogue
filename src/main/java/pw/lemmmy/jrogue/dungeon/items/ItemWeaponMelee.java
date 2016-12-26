package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.entities.actions.ActionMelee;
import pw.lemmmy.jrogue.dungeon.entities.actions.EntityAction;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.utils.Utils;

public abstract class ItemWeaponMelee extends ItemWeapon implements Wieldable {
	@Override
	public void hit(LivingEntity attacker, LivingEntity victim) {
		int baseDamage = calculateDamage(attacker, victim);
		int damage = baseDamage > 0 ? Utils.roll(baseDamage) : baseDamage;

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

	protected int calculateDamage(LivingEntity attacker, LivingEntity victim) {
		int damage = victim.getSize() == LivingEntity.Size.SMALL ? getSmallDamage() : getLargeDamage();

		if (attacker instanceof Player) {
			Player player = (Player) attacker;

			SkillLevel skillLevel = player.getSkillLevel(getSkill());
			int skillModifier = skillLevel.getMeleeWeaponDamage();

			float missChanceMultiplier = 1.0f;

			switch (skillLevel) {
				case ADVANCED:
					missChanceMultiplier = 0.6f;
					break;
				case EXPERT:
					missChanceMultiplier = 0.4f;
					break;
				case MASTER:
					missChanceMultiplier = 0.2f;
					break;
			}

			float missChance = victim.getSize() == LivingEntity.Size.SMALL ?
							   getSmallMissChance() : getLargeMissChance();

			missChance *= missChanceMultiplier;

			if (Utils.randomFloat() < missChance) {
				damage = 0;
			} else {
				damage += skillModifier;
			}
		}

		return damage;
	}

	protected abstract DamageSource getMeleeDamageSource();

	public abstract void onHit(LivingEntity attacker, LivingEntity victim);

	public abstract int getSmallDamage();

	public abstract int getLargeDamage();

	public abstract float getSmallMissChance();

	public abstract float getLargeMissChance();

	public abstract Skill getSkill();

	public void hitLog(String attackerString,
					   String victimString,
					   String neitherString,
					   LivingEntity attacker,
					   LivingEntity victim) {
		if (victim.getHealth() <= 0) { return; }

		if (attacker instanceof Player) {
			attacker.getDungeon().log(attackerString, victim.getName(false));
		} else if (victim instanceof Player) {
			victim.getDungeon().log(victimString, attacker.getName(false));
		} else {
			attacker.getDungeon().log(neitherString, attacker.getName(false), victim.getName(false));
		}
	}
}
