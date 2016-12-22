package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;

public class ItemLongsword extends ItemSword {
	public ItemLongsword() { // unserialisation constructor
		super();
	}

	public ItemLongsword(Level level) { // chest spawning constructor
		super(level);
	}

	public ItemLongsword(Material material) {
		super(material);
	}

	@Override
	public String getSwordName() {
		return "longsword";
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_LONGSWORD;
	}

	@Override
	protected DamageSource getMeleeDamageSource() {
		return DamageSource.LONGSWORD;
	}

	@Override
	public Skill getSkill() {
		return Skill.SKILL_LONGSWORD;
	}

	@Override
	public boolean isTwoHanded() {
		return true;
	}
}
