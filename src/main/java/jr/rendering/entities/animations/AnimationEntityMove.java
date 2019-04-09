package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.rendering.screens.GameScreen;
import jr.utils.Easing;
import jr.utils.VectorInt;

public class AnimationEntityMove extends EntityAnimation {
	private int dx, dy;
	
	public AnimationEntityMove(GameScreen renderer, Entity entity, VectorInt delta) {
		super(renderer, entity);
		
		this.dx = delta.x;
		this.dy = delta.y;
	}
	
	@Override
	public void update(EntityAnimationData data, float t) {
		data.offsetX += Easing.easeInOut(t, -dx, dx, 1);
		data.offsetY += Easing.easeInOut(t, -dy, dy, 1);
		
		if (getEntity() instanceof Player) {
			data.cameraX += Easing.easeInOut(t, -dx, dx, 1);
			data.cameraY += Easing.easeInOut(t, -dy, dy, 1);
		}
	}
}
