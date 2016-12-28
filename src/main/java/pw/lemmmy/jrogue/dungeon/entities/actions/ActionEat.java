package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.comestibles.ItemComestible;

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
			player.consume(item);

			runOnCompleteCallback(entity);
		}
	}
}
