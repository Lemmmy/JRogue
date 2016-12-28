package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.comestibles.ItemComestible;

public class ActionEat extends EntityAction {
	private ItemComestible item;
	
	public ActionEat(Dungeon dungeon, Entity entity, ItemComestible item) {
		this(dungeon, entity, item, null);
	}
	
	public ActionEat(Dungeon dungeon, Entity entity, ItemComestible item, ActionCallback callback) {
		super(dungeon, entity, callback);
		
		this.item = item;
	}
	
	@Override
	public void execute() {
		runBeforeRunCallback();
		
		if (getEntity() instanceof Player) {
			Player player = (Player) getEntity();
			
			player.consume(item);
			
			runOnCompleteCallback();
		}
	}
}
