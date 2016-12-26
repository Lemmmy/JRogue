package pw.lemmmy.jrogue.dungeon.entities.player.roles;

import pw.lemmmy.jrogue.dungeon.entities.player.Attributes;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;

import java.util.List;
import java.util.Map;

public abstract class Role {
	public abstract String getName();
	
	public abstract int getStartingHealth();
	
	public abstract List<ItemStack> getStartingItems();
	
	public abstract ItemStack getStartingLeftHand();
	
	public abstract ItemStack getStartingRightHand();
	
	public abstract Map<Skill, SkillLevel> getStartingSkills();
	
	public abstract void assignAttributes(Attributes attributes);
}
