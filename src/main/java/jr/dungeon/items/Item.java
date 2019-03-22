package jr.dungeon.items;

import com.google.gson.annotations.Expose;
import jr.JRogue;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.events.EventListener;
import jr.dungeon.items.identity.Aspect;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.language.Noun;
import jr.utils.DebugToStringStyle;
import jr.utils.RandomUtils;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@HasRegistry
public abstract class Item implements Serialisable, EventListener {
	@Expose private Map<Class<? extends Aspect>, Aspect> aspects = new HashMap<>();
	@Expose private Set<Class<? extends Aspect>> knownAspects = new HashSet<>();
	
	@Expose private int visualID;
	@Expose private int age;
	
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
	
	public abstract Noun getName(EntityLiving observer);
	
	public Noun getTransformedName(EntityLiving observer) {
		Noun name = getName(observer);
		applyNameTransformers(observer, name);
		return name;
	}
	
	public void applyNameTransformers(EntityLiving observer, Noun noun) {
		getAspects().entrySet().stream()
			.filter(e -> isAspectKnown(observer, e.getKey()))
			.map(Map.Entry::getValue)
			.filter(Objects::nonNull)
			.sorted(Comparator.comparingInt(Aspect::getNamePriority))
			.forEach(a -> a.applyNameTransformers(this, noun));
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
			JRogue.getLogger().error("Error loading item class {}", itemClassName, e);
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
				JRogue.getLogger().error("Error loading aspect class {}", aspectClassName, e);
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
		return itemOptional.orElse(null);
	}
	
	@Override
	public String toString() {
		return toStringBuilder().toString();
	}
	
	public ToStringBuilder toStringBuilder() {
		ToStringBuilder tsb = new ToStringBuilder(this, DebugToStringStyle.STYLE)
			.append("age", String.format("%,d (should age: %s)", age, shouldAge() ? "yes" : "no"))
			.append("visualID", visualID)
			.append("appearance", getAppearance().name().toLowerCase().replace("appearance_", ""))
			.append("category", getCategory().name().toLowerCase());
		
		getAspects().forEach((ac, a) -> tsb.append(a.toStringBuilder()));
		
		return tsb;
	}
}
