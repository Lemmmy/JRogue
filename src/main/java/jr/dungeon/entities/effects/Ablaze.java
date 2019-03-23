package jr.dungeon.entities.effects;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.serialisation.Registered;
import jr.utils.RandomUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Fire status effect.
 */
@Registered(id="statusEffectAblaze")
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

			if (el.getLevel().tileStore.getTileType(el.getX(), el.getY()).isWater()) {
				putOut = true;
				setTurnsPassed(getDuration());
				getMessenger().greenYou("douse the flames in the water!");
				return;
			}

			if (getDamage() >= el.getMaxHealth()) {
				el.kill(new DamageSource(null, null, DamageType.FIRE), getDamage());
			} else {
				el.damage(new DamageSource(null, null, DamageType.FIRE), getDamage());
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
	
	@Override
	public ToStringBuilder toStringBuilder() {
		return super.toStringBuilder()
			.append("putOut", putOut);
	}
}
