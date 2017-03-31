package jr.dungeon.items.weapons;

import jr.dungeon.Level;
import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.Material;

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
	public DamageSourceType getMeleeDamageSourceType() {
		return DamageSourceType.DAGGER;
	}
	
	@Override
	public int getToHitBonus() {
		return 2;
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
