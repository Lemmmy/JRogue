package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;

import java.util.List;

public class MonsterFox extends MonsterCanine {
	public MonsterFox(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Fox" : "fox";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_FOX;
	}
	
	@Override
	public EntityLiving.Size getSize() {
		return EntityLiving.Size.SMALL;
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
	public int getNutritionalValue() {
		return 200;
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
