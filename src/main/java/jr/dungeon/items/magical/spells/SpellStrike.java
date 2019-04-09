package jr.dungeon.items.magical.spells;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.projectiles.EntityStrike;
import jr.dungeon.items.magical.DirectionType;
import jr.dungeon.items.magical.MagicalSchool;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Distance;
import jr.utils.Point;
import jr.utils.RandomUtils;
import jr.utils.VectorInt;

@Registered(id="spellStrike")
public class SpellStrike extends Spell {
	@Override
	public Noun getName() {
		return Lexicon.strike.clone();
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
		castDirectional(caster, VectorInt.ZERO); // cast straight down at the player, dealing splash damage to those around them
	}
	
	@Override
	public void castDirectional(EntityLiving caster, VectorInt direction) {
		if (direction == VectorInt.ZERO) {
			splash(caster, caster.getPosition());
			return;
		}
		
		EntityStrike strike = QuickSpawn.spawnClass(EntityStrike.class, caster.getLevel(), caster.getPosition());
		strike.setDirection(direction);
		strike.setTravelRange(3);
		strike.setSource(caster);
		caster.getLevel().entityStore.addEntity(strike);
	}
	
	private int getDamage() {
		return RandomUtils.roll(2, 12);
	}
	
	private int getSplashRange() {
		return 3;
	}
	
	private void splash(EntityLiving caster, Point position) {
		caster.getLevel().entityStore.getEntities().stream()
			.filter(e -> Distance.i(position, e.getPosition()) <= getSplashRange())
			.filter(EntityLiving.class::isInstance)
			.map(e -> (EntityLiving) e)
			.forEach(e -> e.damage(new DamageSource(caster, null, DamageType.STRIKE_SPELL), getDamage()));
	}
}
