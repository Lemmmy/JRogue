package jr.dungeon.entities.actions;

import jr.dungeon.io.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.tiles.Tile;
import jr.language.LanguageUtils;
import jr.language.transformations.Capitalize;
import jr.utils.Point;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Move/walk action.
 *
 * @see Action
 */
public class ActionMove extends Action {
	private int x;
	private int y;
	
	/**
	 * Move/walk action.
	 *
	 * @param point The position to move to.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link Action.ActionCallback}.
	 */
	public ActionMove(Point point, ActionCallback callback) {
		this(point.getX(), point.getY(), callback);
	}
	
	/**
	 * Move/walk action.
	 *
	 * @param x The X position to move to.
	 * @param y The Y position to move to.
	 * @param callback Callback to call when action-related events occur. See
	 * {@link Action.ActionCallback}.
	 */
	public ActionMove(int x, int y, ActionCallback callback) {
		super(callback);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		List<Entity> unwalkable = entity.getLevel().entityStore.getUnwalkableEntitiesAt(x, y);
		
		if (unwalkable.size() > 0) {
			if (entity instanceof Player) {
				Entity unwalkableEnt = unwalkable.get(0);
				
				if (
					unwalkableEnt.getLastX() != unwalkableEnt.getX() ||
					unwalkableEnt.getLastY() != unwalkableEnt.getY()
				) {
					msg.log(
						"%s beats you to it!",
						LanguageUtils.subject(unwalkableEnt).build(Capitalize.first)
					);
				}
			}
			
			return;
		}
		
		entity.setPosition(x, y);
		
		if (entity instanceof Player) {
			Tile tile = entity.getLevel().tileStore.getTile(x, y);
			
			if (tile.getType().onWalk() != null) {
				msg.log(tile.getType().onWalk());
			}
		}
		
		List<Entity> walkable = entity.getLevel().entityStore.getWalkableEntitiesAt(x, y);
		walkable.forEach(e -> e.walk((EntityLiving) entity));
		
		List<EntityItem> items = walkable.stream().filter(EntityItem.class::isInstance).map(e -> (EntityItem) e)
			.collect(Collectors.toList());
		
		
		if (entity instanceof Player) {
			if (items.size() == 1) {
				ItemStack stack = items.get(0).getItemStack();
				Item item = stack.getItem();
				String verb = !item.isis() && stack.getCount() > 1 ? "are" : "is";
				
				msg.log("There %s [YELLOW]%s[] here.", verb, stack.getName((EntityLiving) entity));
			} else if (items.size() > 1) {
				msg.log("There are [YELLOW]%,d[] items here.", items.size());
			}
		}
		
		runOnCompleteCallback(entity);
	}
}
