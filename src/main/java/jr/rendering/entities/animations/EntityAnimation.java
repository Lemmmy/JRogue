package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.Renderer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class EntityAnimation {
	private Renderer renderer;
	private Entity entity;
	
	/**
	 * Updates the animation. Set entity properties here.
	 *
	 * @param t The animation progress, between 0 and 1.
	 */
	public abstract void update(float t);
	
	public abstract void onTurnLerpStop();
	
	public void setOffset(float x, float y) {
		entity.getPersistence().put("offsetX", x).put("offsetY", y);
	}
}
