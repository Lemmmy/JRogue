package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.screens.GameScreen;
import jr.utils.VectorInt;

public class AnimationEntityDamaged extends EntityAnimation {
	private int deltaX, deltaY;
	
	public AnimationEntityDamaged(GameScreen renderer, Entity entity, Entity attacker) {
		super(renderer, entity);
		
		VectorInt delta = entity.getPosition().clampedDelta(attacker.getPosition());
		deltaX = delta.x; deltaY = delta.y;
	}
	
	@Override
	public void update(EntityAnimationData data, float t) {
		data.offsetX += (float) Math.sin(t * Math.PI) / 4 * deltaX;
		data.offsetY += (float) Math.sin(t * Math.PI) / 4 * deltaY;
		
		data.g *= 1f - (float) Math.sin(t * Math.PI) / 4;
		data.b *= 1f - (float) Math.sin(t * Math.PI) / 4;
	}
}
