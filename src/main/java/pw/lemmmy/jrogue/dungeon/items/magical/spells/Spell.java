package pw.lemmmy.jrogue.dungeon.items.magical.spells;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Serialisable;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.entities.player.roles.Role;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.entities.skills.SkillLevel;
import pw.lemmmy.jrogue.dungeon.items.magical.DirectionType;
import pw.lemmmy.jrogue.dungeon.items.magical.MagicalSchool;

public abstract class Spell implements Serialisable {
	private int knowledgeTimeout = 20000;
	private boolean known = false;
	
	public abstract String getName(boolean requiresCapitalisation);
	
	public abstract MagicalSchool getMagicalSchool();
	
	public abstract DirectionType getDirectionType();
	
	public abstract int getTurnsToRead();
	
	public abstract int getLevel();
	
	public int getKnowledgeTimeout() {
		return knowledgeTimeout;
	}
	
	public void setKnowledgeTimeout(int knowledgeTimeout) {
		this.knowledgeTimeout = knowledgeTimeout;
	}
	
	public boolean isKnown() {
		return known;
	}
	
	public void setKnown(boolean known) {
		this.known = known;
	}
	
	public abstract boolean canCastAtSelf();
	
	public abstract void castNonDirectional(LivingEntity caster);
	
	public abstract void castDirectional(LivingEntity caster, int dx, int dy);
	
	public void update() {
		if (known) {
			knowledgeTimeout--;
		}
		
		if (knowledgeTimeout <= 0) {
			known = false;
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("knowledgeTimeout", knowledgeTimeout);
		obj.put("known", known);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		knowledgeTimeout = obj.getInt("knowledgeTimeout");
		known = obj.getBoolean("known");
	}
	
	public int getCastingCost() {
		return getLevel() * 5;
	}
	
	public int getNutritionCost() {
		return getCastingCost() * 2;
	}
	
	private float getSuccessPenalty(Player player) {
		Role role = player.getRole();
		float penalty = 0;
		
		penalty += role.getSpellcastingSuccessBase();
		
		if (getMagicalSchool() == MagicalSchool.ESCAPE) {
			penalty += role.getSpellcastingSuccessEscape();
		}
		
		// TODO: half the penalty if the player is wearing a robe
		
		return Math.max(0, Math.min(20, penalty));
	}
	
	public float getSuccessChance(Player player) {
		// TODO: Ensure this is balanced
		
		Role role = player.getRole();
		Attribute baseChanceAttribute = role.getSpellcastingSuccessAttribute();
		Skill skill = getMagicalSchool().getSkill();
		SkillLevel skillLevel = player.getSkillLevel(skill);
		
		float penalty = getSuccessPenalty(player);
		float baseChance = player.getAttributes().getAttribute(baseChanceAttribute) * 5.5f;
		float difficulty = getLevel() * 4 - skillLevel.ordinal() * 6 - player.getExperienceLevel() / 3 - 5;
		float chance = baseChance;
		
		if (difficulty > 0) {
			chance -= Math.sqrt(900 * difficulty + 2000);
		} else {
			float learning = 15 * -difficulty / getLevel();
			chance += learning > 20 ? 20 : learning;
		}
		
		chance = Math.max(0, Math.min(120, chance));
		
		float actualChance = chance * (20 - penalty) / 15 - penalty;
		return Math.max(0, Math.min(100, actualChance));
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return getClass().equals(o.getClass());
	}
}
