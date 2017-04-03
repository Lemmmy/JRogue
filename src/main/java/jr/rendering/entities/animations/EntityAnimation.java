package jr.rendering.entities.animations;

import jr.dungeon.entities.Entity;
import jr.rendering.screens.GameScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public abstract class EntityAnimation {
	private GameScreen renderer;
	private Entity entity;
	
	/**
	 * Updates the animation.
	 *
	 * @param t The animation progress, between 0 and 1.
	 */
	public abstract Map<String, Object> update(float t);
	
	public void onTurnLerpStop() {};
}
