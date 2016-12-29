package pw.lemmmy.jrogue.dungeon.entities.player.roles;

import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Attributes;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.Spell;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.SpellStrike;
import pw.lemmmy.jrogue.dungeon.items.weapons.ItemStaff;
import pw.lemmmy.jrogue.utils.RandomUtils;

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
		
		skillMap.put(Skill.SKILL_SPELLS_ATTACK, SkillLevel.EXPERT);
		skillMap.put(Skill.SKILL_SPELLS_HEALING, SkillLevel.ADVANCED);
		skillMap.put(Skill.SKILL_SPELLS_DIVINATION, SkillLevel.EXPERT);
		skillMap.put(Skill.SKILL_SPELLS_ENCHANTMENT, SkillLevel.ADVANCED);
		skillMap.put(Skill.SKILL_SPELLS_CLERICAL, SkillLevel.ADVANCED);
		skillMap.put(Skill.SKILL_SPELLS_ESCAPE, SkillLevel.EXPERT);
		skillMap.put(Skill.SKILL_SPELLS_OTHER, SkillLevel.EXPERT);
		
		return skillMap;
	}
	
	@Override
	public Map<Class<? extends Spell>, Spell> getStartingSpells() {
		Map<Class<? extends Spell>, Spell> spellMap = new HashMap<>();
		
		spellMap.put(SpellStrike.class, new SpellStrike());
		
		return spellMap;
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
	
	@Override
	public int getMaxEnergy() {
		return RandomUtils.roll(1, 3, 4); // 4+d3
	}
	
	@Override
	public int getSpellcastingSuccessBase() {
		return 1;
	}
	
	@Override
	public int getSpellcastingSuccessEscape() {
		return 0;
	}
	
	@Override
	public Attribute getSpellcastingSuccessAttribute() {
		return Attribute.INTELLIGENCE;
	}
}
