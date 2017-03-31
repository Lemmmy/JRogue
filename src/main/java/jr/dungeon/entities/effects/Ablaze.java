package jr.dungeon.entities.effects;

import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.EntityLiving;
import jr.utils.RandomUtils;

/**
 * Fire status effect.
 */
public class Ablaze extends StatusEffect {
	private Severity severity;
	
	private boolean putOut;
	
	/**
	 * Fire status effect. Default duration is 10 to 20 turns.
	 */
	public Ablaze() {
		this(Severity.MAJOR, RandomUtils.random(10, 20));
	}
	
	/**
	 * Fire status effect.
	 *
	 * @param severity How severe the effect is in the HUD. See
	 * {@link jr.dungeon.entities.effects.StatusEffect.Severity}
	 * @param duration How long the effect lasts for.
	 */
	public Ablaze(Severity severity, int duration) {
		super(duration);
		
		this.severity = severity;
	}

	@Override
	public void turn() {
		super.turn();

		if (getEntity() instanceof EntityLiving) {
			EntityLiving el = (EntityLiving) getEntity();

			if (el.getLevel().getTileStore().getTileType(el.getX(), el.getY()).isWater()) {
				putOut = true;
				setTurnsPassed(getDuration());
				getMessenger().greenYou("douse the flames in the water!");
				return;
			}

			if (getDamage() >= el.getMaxHealth()) {
				el.kill(DamageSourceType.FIRE, getDamage(), null);
			} else {
				el.damage(DamageSourceType.FIRE, getDamage(), null);
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
		if (!putOut) {
			getMessenger().greenThe("fire wears off after %d turn%s.", getTurnsPassed(), getTurnsPassed() > 1 ? "s" : "");
		}
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
