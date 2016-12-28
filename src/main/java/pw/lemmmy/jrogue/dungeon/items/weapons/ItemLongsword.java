package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.Material;

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
	public int getToHitBonus() {
		return 0;
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
