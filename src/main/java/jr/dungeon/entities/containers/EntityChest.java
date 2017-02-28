package jr.dungeon.entities.containers;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Shatterable;
import jr.utils.RandomUtils;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityChest extends Entity {
	private Container container;
	@Getter private boolean locked;
	
	public EntityChest(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
		
		container = new Container(getName(null, true));
		locked = RandomUtils.rollD2();
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Chest" : "chest";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_CHEST;
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
		return Optional.of(container);
	}
	
	@Override
	public boolean lootable() {
		return !locked;
	}
	
	@Override
	public Optional<String> lootSuccessString() {
		return Optional.of(String.format("You open the %s...", getName(getDungeon().getPlayer(), false)));
	}
	
	@Override
	public Optional<String> lootFailedString() {
		return Optional.of(String.format("The %s is locked.", getName(getDungeon().getPlayer(), false)));
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		if (e.isKickerPlayer()) {
			boolean somethingShattered = false;
			
			List<Map.Entry<Character, ItemStack>> shatterableItems = container.getItems().entrySet().stream()
				.filter(i -> i.getValue()
					.getItem() instanceof Shatterable)
				.collect(Collectors.toList());
			
			for (Iterator<Map.Entry<Character, ItemStack>> iterator = shatterableItems.iterator(); iterator
				.hasNext(); ) {
				iterator.next();
				
				if (RandomUtils.roll(3) == 1) {
					// kicking chests has a high chance of damaging items regardless of skill.
					iterator.remove();
					somethingShattered = true;
				}
			}
			
			if (somethingShattered) {
				getDungeon().orangeYou("hear something shatter.");
			}
			
			if (locked && RandomUtils.roll(4) == 1) {
				getDungeon().greenThe("%s breaks open!", getName(e.getKicker(), false));
				locked = false;
			}
		}
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
		
		obj.put("locked", locked);
		
		if (getContainer().isPresent()) {
			JSONObject serialisedInventory = new JSONObject();
			getContainer().get().serialise(serialisedInventory);
			
			obj.put("inventory", serialisedInventory);
		}
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		locked = obj.getBoolean("locked");
		
		if (obj.has("inventory")) {
			JSONObject serialisedInventory = obj.getJSONObject("inventory");
			container = Container.createFromJSON(serialisedInventory);
		}
	}
}
