package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Serialisable;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.items.identity.Aspect;
import pw.lemmmy.jrogue.dungeon.items.identity.AspectBeatitude;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Item implements Serialisable {
	private Map<Class<? extends Aspect>, Aspect> aspects = new HashMap<>();
	private List<Class<? extends Aspect>> knownAspects = new ArrayList<>();
	
	private int visualID;
	
	public Item() {
		this.visualID = RandomUtils.random(1000);
		this.aspects.put(AspectBeatitude.class, new AspectBeatitude());
	}
	
	public int getVisualID() {
		return visualID;
	}
	
	public boolean isis() {
		return false;
	}
	
	public boolean beginsWithVowel(LivingEntity observer) {
		return StringUtils.startsWithAny(getName(observer, false, false), "a", "e", "i", "o", "u", "8");
	}
	
	public abstract String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural);
	
	public abstract float getWeight();
	
	public boolean shouldStack() {
		return true;
	}
	
	public boolean equals(Item other) {
		return other.getClass() == getClass() &&
			other.getAppearance() == getAppearance() &&
			other.getAspects() == getAspects();
	}
	
	public Map<Class<? extends Aspect>, Aspect> getAspects() {
		return aspects;
	}
	
	public List<Class<? extends Aspect>> getKnownAspects() {
		return knownAspects;
	}
	
	public List<Aspect> getPersistentAspects() {
		return aspects.values().stream().filter(Aspect::isPersistent).collect(Collectors.toList());
	}
	
	public Optional<Aspect> getAspect(Class<? extends Aspect> aspectClass) {
		return Optional.ofNullable(aspects.get(aspectClass));
	}
	
	public boolean isAspectKnown(Class<? extends Aspect> aspectClass) {
		return knownAspects.contains(aspectClass);
	}
	
	public void observeAspect(Class<? extends Aspect> aspectClass) {
		if (!aspects.containsKey(aspectClass)) {
			return; // can't observe an aspect that doesn't exist!!
		}
		
		knownAspects.add(aspectClass);
	}
	
	public abstract ItemAppearance getAppearance();
	
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
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("class", getClass().getName());
		obj.put("visualID", getVisualID());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		visualID = obj.getInt("visualID");
	}
	
	public Item copy() {
		// /shrug
		
		JSONObject serialisedItem = new JSONObject();
		serialise(serialisedItem);
		
		Optional<Item> itemOptional = createFromJSON(serialisedItem);
		return itemOptional.isPresent() ? itemOptional.get() : null;
	}
}
