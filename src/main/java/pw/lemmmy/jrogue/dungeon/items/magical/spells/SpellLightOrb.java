package pw.lemmmy.jrogue.dungeon.items.magical.spells;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.magic.EntityLightOrb;
import pw.lemmmy.jrogue.dungeon.items.magical.DirectionType;
import pw.lemmmy.jrogue.dungeon.items.magical.MagicalSchool;

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
	
	@Override
	public void castNonDirectional(LivingEntity caster) {
		EntityLightOrb orb = new EntityLightOrb(caster.getDungeon(), caster.getLevel(), caster.getX(), caster.getY());
		caster.getLevel().addEntity(orb);
		
		caster.getDungeon().log("A great orb of light materialises and lights up the dungeon.");
	}
	
	@Override
	public void castDirectional(LivingEntity caster, int dx, int dy) {
		castNonDirectional(caster);
	}
}
