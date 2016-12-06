package pw.lemmmy.jrogue.dungeon.entities.roles;

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

	public abstract int getStrength();

	public abstract int getAgility();

	public abstract int getDexterity();

	public abstract int getConstitution();

	public abstract int getIntelligence();

	public abstract int getWisdom();

	public abstract int getCharisma();

	public abstract float getStrengthRemaining();

	public abstract float getAgilityRemaining();

	public abstract float getDexterityRemaining();

	public abstract float getConstitutionRemaining();

	public abstract float getIntelligenceRemaining();

	public abstract float getWisdomRemaining();

	public abstract float getCharismaRemaining();
}
