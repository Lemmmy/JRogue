package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Serialisable;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.items.identity.Aspect;
import pw.lemmmy.jrogue.dungeon.items.identity.AspectBeatitude;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class Item implements Serialisable {
	private Map<Class<? extends Aspect>, Aspect> aspects = new HashMap<>();
	private Set<Class<? extends Aspect>> knownAspects = new HashSet<>();
	
	private int visualID;
	private int age;
	
	public Item() {
		this.visualID = RandomUtils.random(1000);
		this.aspects.put(AspectBeatitude.class, new AspectBeatitude());
	}
	
	public void update(Entity owner) {
		if (shouldAge()) {
			age++;
		}
	}
	
	public boolean shouldAge() {
		return true;
	}
	
	public int getAge() {
		return age;
	}
	
	public int getVisualID() {
		return visualID;
	}
	
	public boolean isis() {
		return false;
	}
	
	public boolean beginsWithVowel(EntityLiving observer) {
		return StringUtils.startsWithAny(getName(observer, false, false), "a", "e", "i", "o", "u", "8");
	}
	
	public abstract String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural);
	
	public String getBeatitudePrefix(EntityLiving observer, boolean requiresCapitalisation) {
		if (!isAspectKnown(observer, AspectBeatitude.class)) {
			return "";
		}
		
		AtomicReference<String> out = new AtomicReference<>("");
		
		getAspect(AspectBeatitude.class).ifPresent(a -> {
			AspectBeatitude.Beatitude beatitude = ((AspectBeatitude) a).getBeatitude();
			String s = beatitude.name().toLowerCase();
			
			out.set((requiresCapitalisation ? StringUtils.capitalize(s) : s) + " ");
		});
		
		return out.get();
	}
	
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
	
	public Set<Class<? extends Aspect>> getKnownAspects() {
		return knownAspects;
	}
	
	public List<Aspect> getPersistentAspects() {
		return aspects.values().stream().filter(Aspect::isPersistent).collect(Collectors.toList());
	}
	
	public Optional<Aspect> getAspect(Class<? extends Aspect> aspectClass) {
		return Optional.ofNullable(aspects.get(aspectClass));
	}
	
	public boolean isAspectKnown(EntityLiving observer, Class<? extends Aspect> aspectClass) {
		if (aspects.get(aspectClass).isPersistent()) {
			return observer.isAspectKnown(this, aspectClass);
		} else {
			return knownAspects.contains(aspectClass);
		}
	}
	
	public void addAspect(Aspect aspect) {
		aspects.put(aspect.getClass(), aspect);
	}
	
	public void observeAspect(EntityLiving observer, Class<? extends Aspect> aspectClass) {
		if (!aspects.containsKey(aspectClass)) {
			return; // can't observe an aspect that doesn't exist!!
		}
		
		if (aspects.get(aspectClass).isPersistent()) {
			observer.observeAspect(this, aspectClass);
		} else {
			knownAspects.add(aspectClass);
		}
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
		obj.put("age", age);
		
		JSONObject serialisedAspects = new JSONObject();
		aspects.forEach((k, v) -> {
			JSONObject serialisedAspect = new JSONObject();
			v.serialise(serialisedAspect);
			
			serialisedAspects.put(k.getName(), serialisedAspect);
		});
		obj.put("aspects", serialisedAspects);
		
		JSONArray serialisedKnownAspects = new JSONArray();
		knownAspects.forEach(a -> serialisedKnownAspects.put(a.getName()));
		obj.put("knownAspects", serialisedKnownAspects);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void unserialise(JSONObject obj) {
		visualID = obj.getInt("visualID");
		age = obj.optInt("age");
		
		JSONObject serialisedAspects = obj.getJSONObject("aspects");
		serialisedAspects.keySet().forEach(aspectClassName -> {
			JSONObject serialisedAspect = serialisedAspects.getJSONObject(aspectClassName);
			
			try {
				Class<? extends Aspect> aspectClass = (Class<? extends Aspect>) Class.forName(aspectClassName);
				Constructor<? extends Aspect> aspectConstructor = aspectClass.getConstructor();
				
				Aspect aspect = aspectConstructor.newInstance();
				aspect.unserialise(serialisedAspect);
				aspects.put(aspectClass, aspect);
			} catch (ClassNotFoundException e) {
				JRogue.getLogger().error("Unknown aspect class {}", aspectClassName);
			} catch (NoSuchMethodException e) {
				JRogue.getLogger().error("Aspect class {} has no unserialisation constructor", aspectClassName);
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
				JRogue.getLogger().error("Error loading aspect class {}", aspectClassName);
				JRogue.getLogger().error(e);
			}
		});
		
		obj.getJSONArray("knownAspects").forEach(aspectClassName -> {
			try {
				Class<? extends Aspect> aspectClass = (Class<? extends Aspect>) Class.forName((String) aspectClassName);
				knownAspects.add(aspectClass);
			} catch (ClassNotFoundException e) {
				JRogue.getLogger().error("Unknown aspect class {}", aspectClassName);
			}
		});
	}
	
	public Item copy() {
		// /shrug
		
		JSONObject serialisedItem = new JSONObject();
		serialise(serialisedItem);
		
		Optional<Item> itemOptional = createFromJSON(serialisedItem);
		return itemOptional.isPresent() ? itemOptional.get() : null;
	}
}
