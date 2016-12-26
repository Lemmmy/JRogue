package pw.lemmmy.jrogue.dungeon.entities.player.roles;

import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Attributes;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.ItemStaff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleWizard extends Role {
	private ItemStack staff;
	
	@Override
	public String getName() {
		return "Wizard";
	}
	
	@Override
	public int getStartingHealth() {
		return 10;
	}
	
	@Override
	public List<ItemStack> getStartingItems() {
		List<ItemStack> itemList = new ArrayList<>();
		
		staff = new ItemStack(new ItemStaff());
		itemList.add(staff);
		
		return itemList;
	}
	
	@Override
	public ItemStack getStartingLeftHand() {
		return staff;
	}
	
	@Override
	public ItemStack getStartingRightHand() {
		return staff;
	}
	
	@Override
	public Map<Skill, SkillLevel> getStartingSkills() {
		Map<Skill, SkillLevel> skillMap = new HashMap<>();
		
		skillMap.put(Skill.SKILL_STAFF, SkillLevel.BEGINNER);
		
		skillMap.put(Skill.SKILL_SPELLS_ATTACK, SkillLevel.BEGINNER);
		skillMap.put(Skill.SKILL_SPELLS_ENCHANTMENT, SkillLevel.BEGINNER);
		
		return skillMap;
	}
	
	@Override
	public void assignAttributes(Attributes attributes) {
		attributes.setAttribute(Attribute.STRENGTH, 7);
		attributes.setAttribute(Attribute.AGILITY, 7);
		attributes.setAttribute(Attribute.DEXTERITY, 7);
		attributes.setAttribute(Attribute.CONSTITUTION, 7);
		attributes.setAttribute(Attribute.INTELLIGENCE, 10);
		attributes.setAttribute(Attribute.WISDOM, 7);
		attributes.setAttribute(Attribute.CHARISMA, 7);
	}
}
