package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.Renderer;

public class AnimationChestKick extends EntityAnimation {
	public AnimationChestKick(Renderer renderer, Entity entity) {
		super(renderer, entity);
	}
	
	@Override
	public void update(float t) {
		setOffset(0, -((float) Math.sin(t * Math.PI) / 5));
	}
	
	@Override
	public void onTurnLerpStop() {
		setOffset(0, 0);
	}
}
