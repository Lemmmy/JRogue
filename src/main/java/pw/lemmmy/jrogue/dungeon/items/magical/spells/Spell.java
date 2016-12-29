package pw.lemmmy.jrogue.dungeon.items.magical.spells;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.magical.DirectionType;
import pw.lemmmy.jrogue.dungeon.items.magical.MagicalSchool;

public abstract class Spell {
	public abstract String getName(boolean requiresCapitalisation);
	
	public abstract MagicalSchool getMagicalSchool();
	
	public abstract DirectionType getDirectionType();
	
	public abstract int getTurnsToRead();
	
	public abstract int getLevel();
	
	public abstract boolean canCastAtSelf();
	
	public abstract void castNowhere(LivingEntity caster);
	
	public abstract void castDirectional(LivingEntity caster, int dx, int dy);
	
	public int getCastingCost() {
		return getLevel() * 5;
	}
}
