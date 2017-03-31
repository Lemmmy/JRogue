package jr.dungeon.items.weapons;

import jr.dungeon.Level;
import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.Material;

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
	public DamageSourceType getMeleeDamageSourceType() {
		return DamageSourceType.LONGSWORD;
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
