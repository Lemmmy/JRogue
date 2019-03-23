package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.screens.GameScreen;
import jr.utils.Point;

public class AnimationEntityDamaged extends EntityAnimation {
	private Point delta;
	
	public AnimationEntityDamaged(GameScreen renderer, Entity entity, Entity attacker) {
		super(renderer, entity);
		
		delta = entity.getPosition().clampedDelta(attacker.getPosition());
	}
	
	@Override
	public void update(EntityAnimationData data, float t) {
		data.offsetX += (float) Math.sin(t * Math.PI) / 4 * delta.getX();
		data.offsetY += (float) Math.sin(t * Math.PI) / 4 * delta.getY();
		
		data.g *= 1f - (float) Math.sin(t * Math.PI) / 4;
		data.b *= 1f - (float) Math.sin(t * Math.PI) / 4;
	}
}
