package jr.dungeon.items.magical.spells;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.magic.EntityLightOrb;
import jr.dungeon.items.magical.DirectionType;
import jr.dungeon.items.magical.MagicalSchool;

public class SpellLightOrb extends Spell {
	@Override
	public String getName(boolean requiresCapitalisation) {
		return (requiresCapitalisation ? "L" : "l") + "ight orb";
	}
	
	@Override
	public MagicalSchool getMagicalSchool() {
		return MagicalSchool.MATTER;
	}
	
	@Override
	public DirectionType getDirectionType() {
		return DirectionType.NON_DIRECTIONAL;
	}
	
	@Override
	public int getTurnsToRead() {
		return 2;
	}
	
	@Override
	public int getLevel() {
		return 1;
	}
	
	@Override
	public boolean canCastAtSelf() {
		return true;
	}
	
	public int getCastingCost() {
		return getLevel() * 2;
	}
	
	@Override
	public void castNonDirectional(EntityLiving caster) {
		EntityLightOrb orb = new EntityLightOrb(caster.getDungeon(), caster.getLevel(), caster.getX(), caster.getY());
		caster.getLevel().entityStore.addEntity(orb);
		
		caster.getDungeon().log("A great orb of light materialises and lights up the dungeon.");
	}
	
	@Override
	public void castDirectional(EntityLiving caster, int dx, int dy) {
		castNonDirectional(caster);
	}
}
