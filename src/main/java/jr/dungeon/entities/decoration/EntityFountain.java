package jr.dungeon.entities.decoration;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.entities.interfaces.PassiveSoundEmitter;
import jr.dungeon.entities.interfaces.Quaffable;
import jr.dungeon.events.EventHandler;
import jr.dungeon.generators.Climate;

public class EntityFountain extends Entity implements PassiveSoundEmitter, Quaffable {
	public EntityFountain(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	public boolean isFrozen() {
		return getLevel().getClimate() == Climate.COLD;
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Fountain" : "fountain";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return isFrozen() ? EntityAppearance.APPEARANCE_FOUNTAIN_FROZEN :
			   			    EntityAppearance.APPEARANCE_FOUNTAIN;
	}
	
	@Override
	public int getDepth() {
		return 0;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@EventHandler(selfOnly = true)
	public void onWalk(EntityWalkedOnEvent e) {
		if (e.isWalkerPlayer()) {
			getDungeon().log("There is a %s here.", getName(e.getWalker(), false));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public float getSoundProbability() {
		return 0.15f;
	}
	
	@Override
	public String[] getSounds() {
		return new String[] {
			"You hear a light splashing sound.",
			"You hear a light splishing sound.",
			"You hear a light pattering sound.",
			"You hear the splashing of water.",
			"You hear the trickling of water.",
			"You hear the rushing of water.",
			"You hear bubbling water.",
			"You hear gushing water.",
			"You hear water falling on coins.",
			"You hear water pattering on coins.",
		};
	}
	
	@Override
	public void quaff(EntityLiving quaffer) {
		// TODO: fountain magic
		
		quaffer.getDungeon().You("drink from the %s.", getName(quaffer, false));
	}
	
	@Override
	public boolean canQuaff(EntityLiving quaffer) {
		return !isFrozen();
	}
	
	@Override
	public String getQuaffConfirmationMessage(EntityLiving quaffer) {
		return String.format("Drink from the %s?", getName(quaffer, false));
	}
}
