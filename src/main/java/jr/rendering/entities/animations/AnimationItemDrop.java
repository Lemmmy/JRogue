package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.Renderer;

public class AnimationItemDrop extends EntityAnimation {
	public AnimationItemDrop(Renderer renderer, Entity entity) {
		super(renderer, entity);
	}
	
	@Override
	public void update(float t) {
		setOffset(0, -((float) Math.sin(t * Math.PI) / 2));
	}
	
	@Override
	public void onTurnLerpStop() {
		setOffset(0, 0);
	}
}
