package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;

public class ItemDagger extends ItemSword {
	public ItemDagger(Material material) {
		super(material);
	}

	@Override
	public String getSwordName() {
		return "dagger";
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_DAGGER;
	}

	@Override
	protected DamageSource getMeleeDamageSource() {
		return DamageSource.DAGGER;
	}

	@Override
	public Skill getSkill() {
		return Skill.SKILL_DAGGER;
	}

	@Override
	public boolean isTwoHanded() {
		return false;
	}
}
