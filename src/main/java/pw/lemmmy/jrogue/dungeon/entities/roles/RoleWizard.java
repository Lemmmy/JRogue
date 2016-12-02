package pw.lemmmy.jrogue.dungeon.entities.roles;

import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.*;

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
		return null;
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
	public int getStrength() {
		return 7;
	}

	@Override
	public int getAgility() {
		return 7;
	}

	@Override
	public int getDexterity() {
		return 7;
	}

	@Override
	public int getConstitution() {
		return 7;
	}

	@Override
	public int getIntelligence() {
		return 10;
	}

	@Override
	public int getWisdom() {
		return 7;
	}

	@Override
	public int getCharisma() {
		return 7;
	}

	@Override
	public float getStrengthRemaining() {
		return 0.1f;
	}

	@Override
	public float getAgilityRemaining() {
		return 0.1f;
	}

	@Override
	public float getDexterityRemaining() {
		return 0.2f;
	}

	@Override
	public float getConstitutionRemaining() {
		return 0.2f;
	}

	@Override
	public float getIntelligenceRemaining() {
		return 0.3f;
	}

	@Override
	public float getWisdomRemaining() {
		return 0.1f;
	}

	@Override
	public float getCharismaRemaining() {
		return 0.1f;
	}
}
