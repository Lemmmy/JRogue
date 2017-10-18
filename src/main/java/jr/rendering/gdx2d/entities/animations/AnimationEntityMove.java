package jr.rendering.gdx2d.entities.animations;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.rendering.gdx2d.screens.GameScreen;
import jr.utils.Utils;
import jr.utils.Vector;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

public class AnimationEntityMove extends EntityAnimation {
	private int dx, dy;
	
	public AnimationEntityMove(GameScreen renderer, Entity entity, int dx, int dy) {
		super(renderer, entity);
		
		this.dx = dx;
		this.dy = dy;
	}
	
	@Override
	public Map<String, Object> update(float t) {
		val values = new HashMap<String, Object>();
		
		values.put("offset", new Vector(
			Utils.easeInOut(t, -dx, dx, 1),
			Utils.easeInOut(t, -dy, dy, 1)
		));
		
		if (getEntity() instanceof Player) {
			values.put("sceneCamera", new Vector(
				Utils.easeInOut(t, -dx, dx, 1),
				Utils.easeInOut(t, -dy, dy, 1)
			));
		}
		
		return values;
	}
}
