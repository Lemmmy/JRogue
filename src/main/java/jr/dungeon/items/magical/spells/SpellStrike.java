package jr.dungeon.items.magical.spells;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.projectiles.EntityStrike;
import jr.dungeon.items.magical.DirectionType;
import jr.dungeon.items.magical.MagicalSchool;
import jr.utils.RandomUtils;
import jr.utils.Utils;

public class SpellStrike extends Spell {
	@Override
	public String getName(boolean requiresCapitalisation) {
		return (requiresCapitalisation ? "S" : "s") + "trike";
	}
	
	@Override
	public MagicalSchool getMagicalSchool() {
		return MagicalSchool.ATTACK;
	}
	
	@Override
	public DirectionType getDirectionType() {
		return DirectionType.BEAM;
	}
	
	@Override
	public int getTurnsToRead() {
		return 5;
	}
	
	@Override
	public int getLevel() {
		return 1;
	}
	
	@Override
	public boolean canCastAtSelf() {
		return true; // but it's a very bad idea
	}
	
	@Override
	public void castNonDirectional(EntityLiving caster) {
		castDirectional(caster, 0, 0); // cast straight down at the player, dealing splash damage to those around them
	}
	
	@Override
	public void castDirectional(EntityLiving caster, int dx, int dy) {
		if (dx == 0 && dy == 0) {
			splash(caster, caster.getX(), caster.getY());
			return;
		}
		
		EntityStrike strike = new EntityStrike(
			caster.getDungeon(), caster.getLevel(),
			caster.getX(), caster.getY()
		);
		strike.setTravelDirection(dx, dy);
		strike.setTravelRange(3);
		strike.setSource(caster);
		caster.getLevel().getEntityStore().addEntity(strike);
	}
	
	private int getDamage() {
		return RandomUtils.roll(2, 12);
	}
	
	private int getSplashRange() {
		return 3;
	}
	
	private void splash(EntityLiving caster, int x, int y) {
		caster.getLevel().getEntityStore().getEntities().stream()
			.filter(e -> Utils.distance(x, y, e.getX(), e.getY()) <= getSplashRange())
			.map(e -> (EntityLiving) e)
			.forEach(e -> e.damage(DamageSource.STRIKE_SPELL, getDamage(), caster, caster instanceof Player));
	}
}
