package jr.dungeon.entities.magic;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.events.EntityItemDroppedOnEvent;
import jr.dungeon.entities.events.EntityKickedEvent;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.identity.AspectBeatitude;

public class EntityAltar extends Entity {
	public EntityAltar(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return (requiresCapitalisation ? "H" : "h") + "oly altar";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ALTAR;
	}
	
	@DungeonEventHandler
	protected void onKick(EntityKickedEvent e) {
		// TODO: player alignment and luck penalty
	}
	
	@DungeonEventHandler
	protected void onWalk(EntityWalkedOnEvent e) {
		if (e.isWalkerPlayer()) {
			getDungeon().log("There is a %s here.", getName(e.getWalker(), false));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@DungeonEventHandler
	public void onItemDropped(EntityItemDroppedOnEvent e) {
		EntityItem itemEntity = e.getItemEntity();
		Item item = e.getItem();
		ItemStack itemStack = e.getItemStack();
		
		String oldName = itemEntity.getName(getDungeon().getPlayer(), false);
		
		item.observeAspect(getDungeon().getPlayer(), AspectBeatitude.class);
		item.getAspect(AspectBeatitude.class).ifPresent(a -> {
			AspectBeatitude ab = (AspectBeatitude) a;
			
			switch (ab.getBeatitude()) {
				case BLESSED:
					if (item.isis() || itemStack.getCount() > 1) {
						getDungeon().log("There is an amber glow as %s hits the altar.", oldName);
					} else {
						getDungeon().log("There is an amber glow as the %s hits the altar.", oldName);
					}
					
					break;
				case CURSED:
					if (item.isis()) {
						getDungeon().log("A black cloud briefly appears around %s as it hits the altar.", oldName);
					} else if (itemStack.getCount() > 1) {
						getDungeon().log("A black cloud briefly appears around the %s as they hit the altar.", oldName);
					} else {
						getDungeon().log("A black cloud briefly appears around the %s as it hits the altar.", oldName);
					}
					
					break;
			}
		});
	}
}
