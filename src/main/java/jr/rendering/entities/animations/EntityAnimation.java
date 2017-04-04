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
	 *
	 * @return A map containing key names and the new updated values. This map is combined with other animations
	 * (e.g. floats are multiplied, vectors are added etc.). Predefined keys include <code>offset</code>
	 * ({@link jr.utils.Vector}) and <code>r</code>, <code>g</code>, <code>b</code>, <code>a</code> (all
	 * <tt>float</tt>s).
	 */
	public abstract Map<String, Object> update(float t);
	
	public void onTurnLerpStop() {}
}
