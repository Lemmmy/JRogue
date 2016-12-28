package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.Material;

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
