package jr.rendering.gdxvox.objects;

import jr.dungeon.events.EventListener;
import jr.rendering.gdxvox.utils.SceneContext;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractObjectRenderer<ObjectK, ObjectV, BatchT extends VoxelBatch> implements EventListener {
	protected List<ObjectK> objectKeys = new ArrayList<>();
	
	@Setter private BatchT batch;
	
	public abstract void initialiseBatch();
	
	public abstract void objectAdded(ObjectV object, SceneContext scene);
	
	@SuppressWarnings("unchecked")
	public void objectRemoved(ObjectV object, SceneContext scene) {
		batch.remove(object);
	}
}
