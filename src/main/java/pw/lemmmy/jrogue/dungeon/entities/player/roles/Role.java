package pw.lemmmy.jrogue.dungeon.entities.player.roles;

import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Attributes;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.Spell;

import java.util.List;
import java.util.Map;

public abstract class Role {
	public abstract String getName();
	
	public abstract int getStartingHealth();
	
	public abstract List<ItemStack> getStartingItems();
	
	public abstract ItemStack getStartingLeftHand();
	
	public abstract ItemStack getStartingRightHand();
	
	public abstract Map<Skill, SkillLevel> getStartingSkills();
	
	public abstract Map<Class<? extends Spell>, Spell> getStartingSpells();
	
	public abstract void assignAttributes(Attributes attributes);
	
	public abstract int getMaxEnergy();
	
	public abstract int getSpellcastingSuccessBase();
	
	public abstract int getSpellcastingSuccessEscape();
	
	public abstract Attribute getSpellcastingSuccessAttribute();
}
