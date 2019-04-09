package jr.dungeon.items.magical.spells;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.player.roles.Role;
import jr.dungeon.entities.skills.Skill;
import jr.dungeon.entities.skills.SkillLevel;
import jr.dungeon.items.magical.DirectionType;
import jr.dungeon.items.magical.MagicalSchool;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.language.Noun;
import jr.utils.VectorInt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@HasRegistry
public abstract class Spell implements Serialisable {
	@Expose private int knowledgeTimeout = 20000;
	@Expose private boolean known = false;
	
	public abstract Noun getName();
	
	public abstract MagicalSchool getMagicalSchool();
	
	public abstract DirectionType getDirectionType();
	
	public abstract int getTurnsToRead();
	
	public abstract int getLevel();
	
	public abstract boolean canCastAtSelf();
	
	public abstract void castNonDirectional(EntityLiving caster);
	
	public abstract void castDirectional(EntityLiving caster, VectorInt direction);
	
	public void update() {
		if (known) {
			knowledgeTimeout--;
		}
		
		if (knowledgeTimeout <= 0) {
			known = false;
		}
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
