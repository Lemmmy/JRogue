package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.screens.GameScreen;
import jr.utils.Point;
import jr.utils.Vector;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

public class AnimationEntityDamaged extends EntityAnimation {
	private Point delta;
	
	public AnimationEntityDamaged(GameScreen renderer, Entity entity, Entity attacker) {
		super(renderer, entity);
		
		delta = entity.getPosition().clampedDelta(attacker.getPosition());
	}
	
	@Override
	public Map<String, Object> update(float t) {
		val values = new HashMap<String, Object>();
		values.put("offset", new Vector(
			(float) Math.sin(t * Math.PI) / 4 * delta.getX(),
			(float) Math.sin(t * Math.PI) / 4 * delta.getY()
		));
		values.put("g", 1f - (float) Math.sin(t * Math.PI) / 4);
		values.put("b", 1f - (float) Math.sin(t * Math.PI) / 4);
		return values;
	}
}
