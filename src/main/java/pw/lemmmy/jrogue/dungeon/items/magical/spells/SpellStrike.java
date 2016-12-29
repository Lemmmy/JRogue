package pw.lemmmy.jrogue.dungeon.items.magical.spells;

import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityStrike;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.magical.DirectionType;
import pw.lemmmy.jrogue.dungeon.items.magical.MagicalSchool;
import pw.lemmmy.jrogue.utils.RandomUtils;
import pw.lemmmy.jrogue.utils.Utils;

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
	public void castNowhere(LivingEntity caster) {
		castDirectional(caster, 0, 0); // cast straight down at the player, dealing splash damage to those around them
	}
	
	@Override
	public void castDirectional(LivingEntity caster, int dx, int dy) {
		if (dx == 0 && dy == 0) {
			splash(caster, caster.getX(), caster.getY());
			return;
		}
		
		EntityStrike strike = new EntityStrike(caster.getDungeon(), caster.getLevel(), dx, dy);
		strike.setTravelDirection(dx, dy);
		strike.setTravelRange(3);
		caster.getLevel().addEntity(strike);
	}
	
	private int getDamage() {
		return RandomUtils.roll(2, 12);
	}
	
	private int getSplashRange() {
		return 3;
	}
	
	private void splash(LivingEntity caster, int x, int y) {
		caster.getLevel().getEntities().stream()
			.filter(e -> Utils.distance(x, y, e.getX(), e.getY()) <= getSplashRange())
			.map(e -> (LivingEntity) e)
			.forEach(e -> e.damage(DamageSource.STRIKE_SPELL, getDamage(), caster, caster instanceof Player));
	}
}