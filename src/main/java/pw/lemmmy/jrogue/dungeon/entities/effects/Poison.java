package pw.lemmmy.jrogue.dungeon.entities.effects;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public class Poison extends StatusEffect {
	public Poison(Dungeon dungeon, Entity entity) {
		super(dungeon, entity, -1);
	}
	
	@Override
	public void turn() {
		super.turn();
		
		if (getEntity() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) getEntity();
			
			livingEntity.damage(DamageSource.POISON, 1, null, false);
			
			if (getTurnsPassed() >= 15) {
				livingEntity.kill(DamageSource.POISON, 1, null, false);
			}
		}
	}
	
	@Override
	public String getName() {
		return "Poison";
	}
	
	@Override
	public Severity getSeverity() {
		return Severity.CRITICAL;
	}
	
	@Override
	public void onEnd() {
		getDungeon().greenYou("managed to absorb the deadly poison.");
	}
}
