package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.Renderer;
import jr.utils.Vector;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

public class AnimationChestKick extends EntityAnimation {
	public AnimationChestKick(Renderer renderer, Entity entity) {
		super(renderer, entity);
	}
	
	@Override
	public Map<String, Object> update(float t) {
		val values = new HashMap<String, Object>();
		values.put("offset", new Vector(0, -((float) Math.sin(t * Math.PI) / 5)));
		return values;
	}
}
