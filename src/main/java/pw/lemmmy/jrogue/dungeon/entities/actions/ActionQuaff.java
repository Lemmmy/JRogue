package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.items.quaffable.ItemQuaffable;

public class ActionQuaff extends EntityAction {
	private final ItemQuaffable quaffable;
	
	public ActionQuaff(Dungeon dungeon, Entity entity, ItemQuaffable item) {
		this(dungeon, entity, item, null);
	}
	
	public ActionQuaff(Dungeon dungeon, Entity entity, ItemQuaffable item, ActionCallback callback) {
		super(dungeon, entity, callback);
		
		this.quaffable = item;
	}
	
	@Override
	public void execute() {
		runBeforeRunCallback();
		quaffable.quaff(getEntity());
		runOnCompleteCallback();
	}
}
