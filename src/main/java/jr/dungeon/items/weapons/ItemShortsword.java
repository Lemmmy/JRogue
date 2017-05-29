package jr.dungeon.items.weapons;

import jr.dungeon.Level;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.Material;
import jr.language.Lexicon;
import jr.language.Noun;

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
	public Noun getSwordName() {
		return Lexicon.shortsword.clone();
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_SHORTSWORD;
	}
	
	@Override
	public DamageType getMeleeDamageSourceType() {
		return DamageType.SHORTSWORD;
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
