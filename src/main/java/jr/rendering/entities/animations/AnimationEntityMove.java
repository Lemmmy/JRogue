package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.rendering.screens.GameScreen;
import jr.utils.Utils;

public class AnimationEntityMove extends EntityAnimation {
	private int dx, dy;
	
	public AnimationEntityMove(GameScreen renderer, Entity entity, int dx, int dy) {
		super(renderer, entity);
		
		this.dx = dx;
		this.dy = dy;
	}
	
	@Override
	public void update(EntityAnimationData data, float t) {
		data.offsetX += Utils.easeInOut(t, -dx, dx, 1);
		data.offsetY += Utils.easeInOut(t, -dy, dy, 1);
		
		if (getEntity() instanceof Player) {
			data.cameraX += Utils.easeInOut(t, -dx, dx, 1);
			data.cameraY += Utils.easeInOut(t, -dy, dy, 1);
		}
	}
}
