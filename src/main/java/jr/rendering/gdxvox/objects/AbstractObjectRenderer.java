package jr.rendering.gdxvox.objects;

import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.TurnEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractObjectRenderer<ObjectK, ObjectV, BatchT extends VoxelBatch> implements EventListener {
	protected List<ObjectK> objectKeys = new ArrayList<>();
	
	@Setter private BatchT batch;
	
	public abstract void initialiseBatch();
	
	public abstract void objectAdded(ObjectV object);
	
	public void objectRemoved(ObjectV object) {
		batch.remove(object);
	}
	
	public void update() {
		batch.update();
	}
	
	@EventHandler
	public void onTurn(TurnEvent e) {
		update();
	}
}
