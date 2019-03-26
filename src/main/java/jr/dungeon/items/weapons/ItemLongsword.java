package jr.dungeon.items.weapons;

import jr.dungeon.Level;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.Material;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="itemLongsword")
public class ItemLongsword extends ItemSword {
	public ItemLongsword() { // deserialisation constructor
		super();
	}
	
	public ItemLongsword(Level level) { // chest spawning constructor
		super(level);
	}
	
	public ItemLongsword(Material material) {
		super(material);
	}
	
	@Override
	public Noun getSwordName() {
		return Lexicon.longsword.clone();
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_LONGSWORD;
	}
	
	@Override
	public DamageType getMeleeDamageSourceType() {
		return DamageType.LONGSWORD;
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
