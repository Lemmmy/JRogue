package jr.dungeon.entities.actions;

import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.player.visitors.PlayerConsume;
import jr.dungeon.items.comestibles.ItemComestible;

public class ActionEat extends EntityAction {
	private final ItemComestible item;
	
	public ActionEat(ItemComestible item, ActionCallback callback) {
		super(callback);
		this.item = item;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			player.defaultVisitors.consume(item);
			
			runOnCompleteCallback(entity);
		}
	}
}
