package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import lombok.Getter;

@Getter
public class AbstractObjectRendererInstance<ObjectV> {
	private ObjectV objectInstance;
	private ModelInstance modelInstance;
	
	public AbstractObjectRendererInstance(ObjectV objectInstance, ModelInstance modelInstance) {
		this.objectInstance = objectInstance;
		this.modelInstance = modelInstance;
	}
}
