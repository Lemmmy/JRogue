package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;

public class ItemShortsword extends ItemSword {
	public ItemShortsword() { // unserialisation constructor
		super();
	}

	public ItemShortsword(Level level) { // chest spawning constructor
		super(level);
	}

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
	protected DamageSource getMeleeDamageSource() {
		return DamageSource.SHORTSWORD;
	}

	@Override
	public float getSmallMissChance() {
		return 0.2f;
	}

	@Override
	public float getLargeMissChance() {
		return 0.1f;
	}

	@Override
	public Skill getSkill() {
		return Skill.SKILL_SHORTSWORD;
	}

	@Override
	public boolean isTwoHanded() {
		return false;
	}
}
