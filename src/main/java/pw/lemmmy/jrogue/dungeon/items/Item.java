package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public abstract class Item{
	private int visualID;

	private boolean identified = false;
	private BUCStatus bucStatus = BUCStatus.UNCURSED;

	public Item() {
		this.visualID = Utils.random(1000);
	}

	public int getVisualID() {
		return visualID;
	}

	public boolean isIdentified() {
		return identified;
	}

	public void setIdentified(boolean identified) {
		this.identified = identified;
	}

	public boolean isis() {
		return false;
	}

	public boolean beginsWithVowel() {
		return StringUtils.startsWithAny(getName(false, false), "a", "e", "i", "o", "u", "8");
	}

	public abstract String getName(boolean requiresCapitalisation, boolean plural);

	public abstract float getWeight();

	public boolean equals(Item other) {
		return other.getClass() == getClass() &&
			other.getAppearance() == getAppearance() &&
			other.getBUCStatus() == getBUCStatus();
	}

	public abstract ItemAppearance getAppearance();

	public BUCStatus getBUCStatus() {
		return bucStatus;
	}

	public void setBUCStatus(BUCStatus bucStatus) {
		this.bucStatus = bucStatus;
	}

	public abstract ItemCategory getCategory();

	@SuppressWarnings("unchecked")
	public static Optional<Item> createFromJSON(JSONObject serialisedItem) {
		String itemClassName = serialisedItem.getString("class");

		try {
			Class<? extends Item> itemClass = (Class<? extends Item>) Class.forName(itemClassName);
			Constructor<? extends Item> itemConstructor = itemClass.getConstructor();

			Item item = itemConstructor.newInstance();
			item.unserialise(serialisedItem);
			return Optional.of(item);
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown item class {}", itemClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Item class {} has no unserialisation constructor", itemClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading item class {}", itemClassName);
			JRogue.getLogger().error(e);
		}

		return Optional.empty();
	}

	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		obj.put("visualID", getVisualID());
		obj.put("identified", isIdentified());
		obj.put("buc", getBUCStatus().name());
	}

	public void unserialise(JSONObject obj) {
		visualID = obj.getInt("visualID");
		identified = obj.getBoolean("identified");
		bucStatus = BUCStatus.valueOf(obj.getString("buc"));
	}

	public Item copy() {
		// /shrug

		JSONObject serialisedItem = new JSONObject();
		serialise(serialisedItem);

		Optional<Item> itemOptional = createFromJSON(serialisedItem);
		return itemOptional.isPresent() ? itemOptional.get() : null;
	}

	public enum BUCStatus {
		BLESSED,
		UNCURSED,
		CURSED
	}
}
