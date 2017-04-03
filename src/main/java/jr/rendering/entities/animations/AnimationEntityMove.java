package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.Renderer;
import jr.utils.Utils;

public class AnimationEntityMove extends EntityAnimation {
	private int dx, dy;
	
	public AnimationEntityMove(Renderer renderer, Entity entity, int dx, int dy) {
		super(renderer, entity);
		
		this.dx = dx;
		this.dy = dy;
	}
	
	@Override
	public void update(float t) {
		setOffset(
			Utils.easeInOut(t, -dx, dx, 1),
			Utils.easeInOut(t, -dy, dy, 1)
		);
	}
	
	@Override
	public void onTurnLerpStop() {
		setOffset(0, 0);
	}
}
