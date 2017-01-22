package jr.dungeon.items.weapons;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.Material;
import jr.dungeon.Level;
import jr.dungeon.items.ItemAppearance;

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
	public DamageSource getMeleeDamageSource() {
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
