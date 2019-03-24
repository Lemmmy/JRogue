package jr.dungeon.entities.magic;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.entities.events.ItemDroppedOnEntityEvent;
import jr.dungeon.events.EventHandler;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.dungeon.serialisation.Registered;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="entityAltar")
public class EntityAltar extends Entity {
	public EntityAltar(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	protected EntityAltar() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.holyAltar.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ALTAR;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		// TODO: player alignment and luck penalty
	}
	
	@EventHandler(selfOnly = true)
	public void onWalk(EntityWalkedOnEvent e) {
		if (e.isWalkerPlayer()) {
			getDungeon().log("There is %s here.", LanguageUtils.anObject(this));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@EventHandler(selfOnly = true)
	public void onItemDropped(ItemDroppedOnEntityEvent e) {
		EntityItem itemEntity = e.getItemEntity();
		Item item = e.getItem();
		ItemStack itemStack = e.getItemStack();
		
		Noun oldName = LanguageUtils.object(getDungeon().getPlayer(), itemEntity.getItem());
		
		item.observeAspect(getDungeon().getPlayer(), AspectBeatitude.class);
		item.getAspect(AspectBeatitude.class).ifPresent(a -> {
			AspectBeatitude ab = (AspectBeatitude) a;
			
			switch (ab.getBeatitude()) {
				case BLESSED:
					getDungeon().log("There is an amber glow as %s hits the altar.", oldName);
					break;
				case CURSED:
					getDungeon().log("A black cloud briefly appears around %s as it hits the altar.", oldName);
					break;
			}
		});
	}
}
