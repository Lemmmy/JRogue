package jr.dungeon.entities.magic;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.dungeon.entities.Entity;

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
	
	@Override
	protected void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		// TODO: player alignment and luck penalty
	}
	
	@Override
	protected void onWalk(EntityLiving walker, boolean isPlayer) {
		if (isPlayer) {
			getDungeon().log("There is an altar here.", getName(walker, false));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public void onItemDropped(EntityItem entityItem) {
		super.onItemDropped(entityItem);
		
		String oldName = entityItem.getName(getDungeon().getPlayer(), false);
		
		entityItem.getItem().observeAspect(getDungeon().getPlayer(), AspectBeatitude.class);
		entityItem.getItem().getAspect(AspectBeatitude.class).ifPresent(a -> {
			AspectBeatitude ab = (AspectBeatitude) a;
			
			switch (ab.getBeatitude()) {
				case BLESSED:
					if (entityItem.getItem().isis() || entityItem.getItemStack().getCount() > 1) {
						getDungeon().log("There is an amber glow as %s hits the altar.", oldName);
					} else {
						getDungeon().log("There is an amber glow as the %s hits the altar.", oldName);
					}
					
					break;
				case CURSED:
					if (entityItem.getItem().isis()) {
						getDungeon().log("A black cloud briefly appears around %s as it hits the altar.", oldName);
					} else if (entityItem.getItemStack().getCount() > 1) {
						getDungeon().log("A black cloud briefly appears around the %s as they hit the altar.", oldName);
					} else {
						getDungeon().log("A black cloud briefly appears around the %s as it hits the altar.", oldName);
					}
					
					break;
			}
		});
	}
}
