package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.screens.GameScreen;

public class AnimationChestKick extends EntityAnimation {
	public AnimationChestKick(GameScreen renderer, Entity entity) {
		super(renderer, entity);
	}
	
	@Override
	public void update(EntityAnimationData data, float t) {
		data.offsetY -= (float) Math.sin(t * Math.PI) / 5;
	}
}
