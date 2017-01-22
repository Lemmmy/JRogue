package jr.dungeon.items.magical;

import jr.dungeon.entities.skills.Skill;

public enum MagicalSchool {
	ATTACK(Skill.SKILL_SPELLS_ATTACK),
	CLERICAL(Skill.SKILL_SPELLS_CLERICAL),
	DIVINATION(Skill.SKILL_SPELLS_DIVINATION),
	ENCHANTMENT(Skill.SKILL_SPELLS_ENCHANTMENT),
	ESCAPE(Skill.SKILL_SPELLS_ESCAPE),
	HEALING(Skill.SKILL_SPELLS_HEALING),
	MATTER(Skill.SKILL_SPELLS_OTHER),
	OTHER(Skill.SKILL_SPELLS_OTHER);
	
	private Skill skill;
	
	MagicalSchool(Skill skill) {
		this.skill = skill;
	}
	
	public Skill getSkill() {
		return skill;
	}
}
