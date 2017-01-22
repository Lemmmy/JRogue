package jr.dungeon.entities.actions;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.player.Player;
import jr.dungeon.tiles.Tile;
import jr.dungeon.Messenger;
import jr.dungeon.entities.Entity;
import jr.dungeon.items.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class ActionMove extends EntityAction {
	private int x;
	private int y;
	
	public ActionMove(int x, int y, ActionCallback callback) {
		super(callback);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void execute(Entity entity, Messenger msg) {
		runBeforeRunCallback(entity);
		
		List<Entity> unwalkable = entity.getLevel().getUnwalkableEntitiesAt(x, y);
		
		if (unwalkable.size() > 0) {
			if (entity instanceof Player) {
				Entity unwalkableEnt = unwalkable.get(0);
				
				if (
					unwalkableEnt.getLastX() != unwalkableEnt.getX() ||
					unwalkableEnt.getLastY() != unwalkableEnt.getY()
				) {
					msg.The("%s beats you to it!", unwalkableEnt.getName((EntityLiving) entity, false));
				}
			}
			
			return;
		}
		
		entity.setPosition(x, y);
		
		if (entity instanceof Player) {
			Tile tile = entity.getLevel().getTile(x, y);
			
			if (tile.getType().onWalk() != null) {
				msg.log(tile.getType().onWalk());
			}
		}
		
		List<Entity> walkable = entity.getLevel().getWalkableEntitiesAt(x, y);
		walkable.forEach(e -> e.walk((EntityLiving) entity, entity instanceof Player));
		
		List<EntityItem> items = walkable.stream().filter(EntityItem.class::isInstance).map(e -> (EntityItem) e)
			.collect(Collectors.toList());
		
		
		if (entity instanceof Player) {
			if (items.size() == 1) {
				ItemStack stack = items.get(0).getItemStack();
				
				if (stack.getItem().isis()) {
					msg.log("There is [YELLOW]%s[] here.", stack.getName((EntityLiving) entity, false));
				} else {
					if (stack.getCount() > 1) {
						msg.log("There are [YELLOW]%s[] here.", stack.getName((EntityLiving) entity, false));
					} else {
						msg.log(
							"There is %s [YELLOW]%s[] here.",
							stack.beginsWithVowel((EntityLiving) entity) ? "an" : "a", stack.getName((EntityLiving) entity, false)
						);
					}
				}
			} else if (items.size() > 1) {
				msg.log("There are [YELLOW]%d[] items here.", items.size());
			}
		}
		
		runOnCompleteCallback(entity);
	}
}
