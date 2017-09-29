package jr.rendering.gdxvox.objects;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import jr.dungeon.events.EventListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class AbstractObjectRenderer<ObjectK, ObjectV, RendererInstanceT extends
	AbstractObjectRendererInstance> implements EventListener, RenderableProvider {
	protected List<ObjectK> objectKeys = new ArrayList<>();
	protected Map<ObjectV, RendererInstanceT> objectInstanceMap = new HashMap<>();
	
	public abstract void objectAdded(ObjectV object);
	
	public void objectRemoved(ObjectV object) {
		objectInstanceMap.remove(object);
	}
	
	public void renderAll(ModelBatch batch) {
		batch.render(this);
	}
	
	public abstract boolean shouldDraw(RendererInstanceT instance);
}
