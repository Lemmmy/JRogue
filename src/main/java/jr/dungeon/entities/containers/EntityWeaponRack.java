package jr.dungeon.entities.containers;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.entities.interfaces.Lootable;
import jr.dungeon.events.DungeonEventHandler;
import org.json.JSONObject;

import java.util.Optional;

public class EntityWeaponRack extends Entity implements Lootable {
	private Container container;
	
	public EntityWeaponRack(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		container = new WeaponRackContainer(getName(null, true));
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Weapon rack" : "weapon rack";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return container.isEmpty() ? EntityAppearance.APPEARANCE_WEAPON_RACK
								   : EntityAppearance.APPEARANCE_WEAPON_RACK_STOCKED;
	}
	
	@Override
	public int getDepth() {
		return 1;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@Override
	public Optional<Container> getContainer() {
		return Optional.ofNullable(container);
	}

	@Override
	public Optional<String> getLootSuccessString() {
		return Optional.of(String.format("You browse the %s.", getName(getDungeon().getPlayer(), false)));
	}
	
	@DungeonEventHandler(selfOnly = true)
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
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		if (getContainer().isPresent()) {
			JSONObject serialisedInventory = new JSONObject();
			getContainer().get().serialise(serialisedInventory);
			
			obj.put("inventory", serialisedInventory);
		}
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		if (obj.has("inventory")) {
			JSONObject serialisedInventory = obj.getJSONObject("inventory");
			container = Container.createFromJSON(WeaponRackContainer.class, serialisedInventory);
		}
	}
}
