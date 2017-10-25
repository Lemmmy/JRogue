package jr.rendering.gdxvox.objects;

import jr.dungeon.events.EventListener;
import jr.rendering.gdxvox.context.SceneContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractObjectRenderer<
	ObjectK, ObjectV,
	BatchT extends VoxelBatch,
	ManagerT extends AbstractObjectRendererManager>
	implements EventListener {
	protected List<ObjectK> objectKeys = new ArrayList<>();
	
	protected ManagerT manager;
	protected SceneContext scene;
	
	public void onAddedToMap(ManagerT manager, SceneContext scene) {
		this.manager = manager;
		this.scene = scene;
	}
	
	public abstract void objectAdded(ObjectV object);
	
	@SuppressWarnings("unchecked")
	public void objectRemoved(ObjectV object) {
		manager.removeObjectInstance(object);
	}
}
