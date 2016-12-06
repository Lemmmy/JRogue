package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;

public class ItemShortsword extends ItemSword {
	public ItemShortsword(Material material) {
		super(material);
	}

	@Override
	public String getSwordName() {
		return "shortsword";
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_SHORTSWORD;
	}

	@Override
	public Skill getSkill() {
		return Skill.SKILL_SHORTSWORD;
	}

	@Override
	protected DamageSource getMeleeDamageSource() {
		return DamageSource.SHORTSWORD;
	}

	@Override
	public boolean isTwoHanded() {
		return false;
	}
}
