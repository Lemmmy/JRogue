package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Messenger;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;

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
					msg.The("%s beats you to it!", unwalkableEnt.getName((LivingEntity) entity, false));
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
		walkable.forEach(e -> e.walk((LivingEntity) entity, entity instanceof Player));
		
		List<EntityItem> items = walkable.stream().filter(EntityItem.class::isInstance).map(e -> (EntityItem) e)
			.collect(Collectors.toList());
		
		
		if (entity instanceof Player) {
			if (items.size() == 1) {
				ItemStack stack = items.get(0).getItemStack();
				
				if (stack.getItem().isis()) {
					msg.log("There is [YELLOW]%s[] here.", stack.getName((LivingEntity) entity, false));
				} else {
					if (stack.getCount() > 1) {
						msg.log("There are [YELLOW]%s[] here.", stack.getName((LivingEntity) entity, false));
					} else {
						msg.log(
							"There is %s [YELLOW]%s[] here.",
							stack.beginsWithVowel((LivingEntity) entity) ? "an" : "a", stack.getName((LivingEntity) entity, false)
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
