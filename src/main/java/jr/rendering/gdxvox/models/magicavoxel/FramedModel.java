package jr.rendering.gdxvox.models.magicavoxel;

import com.badlogic.gdx.graphics.g3d.Model;
import lombok.Getter;

@Getter
public class FramedModel {
	private Model[] frames;
	
	public FramedModel(Model[] frames) {
		this.frames = frames;
	}
}
