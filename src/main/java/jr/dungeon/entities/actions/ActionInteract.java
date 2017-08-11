package jr.dungeon.entities.actions;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.interfaces.Interactive;
import jr.dungeon.io.Messenger;
import lombok.Getter;

public class ActionInteract extends Action {
	@Getter private Interactive target;
	
	public ActionInteract(ActionCallback callback, Interactive target) {
		super(callback);
		this.target = target;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		target.interact(entity);
		runOnCompleteCallback(entity);
	}
}
