package jr.rendering.gdx2d.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import jr.rendering.base.components.RendererComponent;
import jr.rendering.gdx2d.screens.GameScreen;
import lombok.Getter;
import lombok.Setter;

public abstract class GameComponent extends RendererComponent<GameScreen> {
	@Getter @Setter public OrthographicCamera camera;
	
	/**
	 * A component that gets a chance to render on the screen at the specified Z-index. Lower Z-indexes are drawn first.
	 *
	 * @param gameScreen The renderer that uses this component.
	 */
	public GameComponent(GameScreen gameScreen) {
		super(gameScreen);
	}
}
