package pw.lemmmy.jrogue.dungeon.entities.containers;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

import java.util.Optional;

public class EntityWeaponRack extends Entity {
	private Container container;
	
	public EntityWeaponRack(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		container = new Container(getName(true));
	}
	
	@Override
	public String getName(boolean requiresCapitalisation) {
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
	public boolean lootable() {
		return true;
	}
	
	@Override
	public Optional<String> lootSuccessString() {
		return Optional.of(String.format("You browse the %s.", getName(false)));
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {}
	
	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
		getDungeon().log("There is a %s here.", getName(false));
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
			container = Container.createFromJSON(serialisedInventory);
		}
	}
}
