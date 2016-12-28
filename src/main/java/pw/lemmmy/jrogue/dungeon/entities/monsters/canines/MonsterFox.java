package pw.lemmmy.jrogue.dungeon.entities.monsters.canines;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;

import java.util.List;

public class MonsterFox extends MonsterCanine {
	public MonsterFox(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		if (getAI() instanceof GhoulAI) {
			((GhoulAI) getAI()).setAttackProbability(0.65f);
		}
	}
	
	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Fox" : "fox";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_FOX;
	}
	
	@Override
	public Size getSize() {
		return Size.SMALL;
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED + 3;
	}
	
	@Override
	public int getWeight() {
		return 250;
	}
	
	@Override
	public int getNutrition() {
		return 200;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		return null;
	}
	
	@Override
	public int getVisibilityRange() {
		return 15;
	}
}
