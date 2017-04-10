package jr.dungeon.entities.player.roles;

import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Attributes;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.entities.skills.SkillLevel;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.magical.spells.Spell;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Role {
	@Getter private static final Set<Class<? extends Role>> roles = new HashSet<>();
	
	public static void registerRole(Class<? extends Role> roleClass) {
		roles.add(roleClass);
	}
	
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
