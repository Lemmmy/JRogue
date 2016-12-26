package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;

public class ItemDagger extends ItemSword {
	public ItemDagger() { // unserialisation constructor
		super();
	}

	public ItemDagger(Level level) { // chest spawning constructor
		super(level);
	}

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
	public float getSmallMissChance() {
		return 0.3f;
	}

	@Override
	public float getLargeMissChance() {
		return 0.1f;
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
