package pw.lemmmy.jrogue.dungeon.entities.containers;

import org.json.JSONObject;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.Shatterable;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityChest extends Entity {
	private Container container;
	private boolean locked;
	
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
	
	@Override
	protected void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		if (isPlayer) {
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
				getDungeon().greenThe("%s breaks open!", getName(getDungeon().getPlayer(), false));
				locked = false;
			}
		}
	}
	
	@Override
	protected void onWalk(EntityLiving walker, boolean isPlayer) {
		if (isPlayer) {
			getDungeon().log("There is a %s here.", getName(getDungeon().getPlayer(), false));
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
