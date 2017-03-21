package jr.rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public interface ScreenTransition {
	void render(Batch batch, Texture currentScreenTexture, Texture nextScreenTexture, float percent);
}
