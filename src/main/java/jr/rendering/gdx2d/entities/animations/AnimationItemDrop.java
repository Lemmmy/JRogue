package jr.rendering.gdx2d.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.gdx2d.screens.GameScreen;
import jr.utils.Vector;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

public class AnimationItemDrop extends EntityAnimation {
	public AnimationItemDrop(GameScreen renderer, Entity entity) {
		super(renderer, entity);
	}
	
	@Override
	public Map<String, Object> update(float t) {
		val values = new HashMap<String, Object>();
		values.put("offset", new Vector(0, -((float) Math.sin(t * Math.PI) / 2)));
		return values;
	}
}
