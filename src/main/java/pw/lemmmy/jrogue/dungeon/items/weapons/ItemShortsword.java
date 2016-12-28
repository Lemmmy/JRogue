package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.Material;

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
	public int getToHitBonus() {
		return 0;
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
