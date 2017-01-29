package jr.dungeon.entities.effects;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityLiving;
import jr.utils.RandomUtils;

public class Ablaze extends StatusEffect {

	private Severity severity;
	public Ablaze() {
		this(Severity.MAJOR, RandomUtils.random(10, 20));
	}

	public Ablaze(Severity s, int d) {
		super(d);
		this.severity = s;
	}

	@Override
	public void turn() {
		super.turn();

		if (getEntity() instanceof EntityLiving) {
			EntityLiving el = (EntityLiving) getEntity();

			if (el.getLevel().getTileStore().getTileType(el.getX(), el.getY()).isWater()) {
				setTurnsPassed(getDuration());
				return;
			}

			if (getDamage() == 0) {
				// fuck off lignum
			} else if(getDamage() >= el.getMaxHealth()) {
				el.kill(DamageSource.FIRE, getDamage(), null);
			} else {
				el.damage(DamageSource.FIRE, getDamage(), null);
			}
		}
	}

	@Override
	public String getName() {
		return "Ablaze";
	}

	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public void onEnd() {
		getMessenger().greenThe("fire wears off after %d turn%s.", getTurnsPassed(), getTurnsPassed() > 1 ? "s" : "");
	}

	public int getDamage() {
		switch(severity) {
			case CRITICAL:
				return 2;
			case MAJOR:
				return getTurnsPassed() % 2 == 0 ? 1 : 0;
			case MINOR:
				return getTurnsPassed() % 4 == 0 ? 1 : 0;
		}
		return 0;
	}

}
