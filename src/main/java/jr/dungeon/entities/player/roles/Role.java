package jr.dungeon.entities.player.roles;

import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Attributes;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.entities.skills.SkillLevel;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.magical.spells.Spell;

import java.util.List;
import java.util.Map;

public abstract class Role {
	public abstract String getName();
	
	public abstract int getStartingHealth();
	
	public abstract List<ItemStack> getStartingItems();
	
	public abstract ItemStack getStartingLeftHand();
	
	public abstract ItemStack getStartingRightHand();
	
	public abstract Map<Skill, SkillLevel> getStartingSkills();
	
	public abstract Map<Character, Spell> getStartingSpells();
	
	public abstract void assignAttributes(Attributes attributes);
	
	public abstract int getMaxEnergy();
	
	public abstract int getSpellcastingSuccessBase();
	
	public abstract int getSpellcastingSuccessEscape();
	
	public abstract Attribute getSpellcastingSuccessAttribute();
}
