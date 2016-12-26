package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityItem;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.tiles.Tile;

import java.util.List;
import java.util.stream.Collectors;

public class ActionMove extends EntityAction {
	private int x;
	private int y;
	
	public ActionMove(Dungeon dungeon, Entity entity, int x, int y) {
		super(dungeon, entity);
		
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void execute() {
		runBeforeRunCallback();
		
		List<Entity> unwalkable = getEntity().getLevel().getUnwalkableEntitiesAt(x, y);
		
		if (unwalkable.size() > 0) {
			if (getEntity() instanceof Player) {
				Entity entity = unwalkable.get(0);
				
				if (entity.getLastX() != entity.getX() || entity.getLastY() != entity.getY()) {
					getDungeon().The("%s beats you to it!", entity.getName(false));
				}
			}
			
			return;
		}
		
		getEntity().setPosition(x, y);
		
		if (getEntity() instanceof Player) {
			Tile tile = getEntity().getLevel().getTile(x, y);
			
			if (tile.getType().onWalk() != null) {
				getDungeon().log(tile.getType().onWalk());
			}
		}
		
		List<Entity> walkable = getEntity().getLevel().getWalkableEntitiesAt(x, y);
		walkable.forEach(e -> e.walk((LivingEntity) getEntity(), getEntity() instanceof Player));
		
		List<EntityItem> items = walkable.stream().filter(EntityItem.class::isInstance).map(e -> (EntityItem) e)
			.collect(Collectors.toList());
		
		if (items.size() == 1) {
			ItemStack stack = items.get(0).getItemStack();
			
			if (stack.getItem().isis()) {
				getDungeon().log("There is [YELLOW]%s[] here.", stack.getName(false));
			} else {
				if (stack.getCount() > 1) {
					getDungeon().log("There are [YELLOW]%s[] here.", stack.getName(false));
				} else {
					getDungeon().log(
						"There is %s [YELLOW]%s[] here.",
						stack.beginsWithVowel() ? "an" : "a", stack.getName(false)
					);
				}
			}
		} else if (items.size() > 1) {
			getDungeon().log("There are [YELLOW]%d[] items here.", items.size());
		}
		
		runOnCompleteCallback();
	}
}
