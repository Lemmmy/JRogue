package jr.dungeon.items.weapons;

import jr.dungeon.Level;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.Material;
import jr.language.Lexicon;
import jr.language.Noun;

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
	public Noun getSwordName() {
		return Lexicon.dagger.clone();
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_DAGGER;
	}
	
	@Override
	public DamageType getMeleeDamageSourceType() {
		return DamageType.DAGGER;
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
