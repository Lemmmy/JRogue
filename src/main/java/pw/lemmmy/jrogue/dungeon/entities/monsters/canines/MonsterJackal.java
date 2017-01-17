package pw.lemmmy.jrogue.dungeon.entities.monsters.canines;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

import java.util.List;

public class MonsterJackal extends MonsterCanine {
	public MonsterJackal(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Jackal" : "jackal";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_JACKAL;
	}
	
	@Override
	public Size getSize() {
		return EntityLiving.Size.SMALL;
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED;
	}
	
	@Override
	public int getWeight() {
		return 300;
	}
	
	@Override
	public int getNutrition() {
		return 250;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public int getVisibilityRange() {
		return 15;
	}
}
