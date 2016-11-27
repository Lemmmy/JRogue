package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.items.ItemComestible;

public class ActionEat extends EntityAction {
	private ItemComestible item;
	private EntityItem entityItem;

	public ActionEat(Dungeon dungeon, Entity entity, ItemComestible item) {
		this(dungeon, entity, item, null);
	}

	public ActionEat(Dungeon dungeon, Entity entity, ItemComestible item, EntityItem entityItem) {
		super(dungeon, entity);

		this.item = item;
		this.entityItem = entityItem;
	}

	@Override
	public void execute() {
		runBeforeRunCallback();

		if (getEntity() instanceof Player) {
			Player player = (Player) getEntity();

			ItemComestible.EatenState state = player.consume(item);
			item.setEatenState(state);

			if (entityItem != null && state == ItemComestible.EatenState.EATEN) {
				entityItem.getLevel().removeEntity(entityItem);
			}

			runOnCompleteCallback();
		}
	}
}
