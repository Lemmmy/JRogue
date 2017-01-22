package jr.dungeon.items.comestibles;

import jr.ErrorHandler;
import jr.JRogue;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.FoodPoisoning;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.identity.AspectBeatitude;
import org.json.JSONObject;
import jr.dungeon.items.identity.AspectRottenness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCorpse extends ItemComestible {
	private EntityLiving entity;
	
	public ItemCorpse() { // unserialisation constructor
		super();
		
		addAspect(new AspectRottenness());
	}
	
	public ItemCorpse(EntityLiving entity) {
		super();
		
		this.entity = entity;
		
		addAspect(new AspectRottenness());
	}
	
	@Override
	public void update(Entity owner) {
		super.update(owner);
		
		if (
			owner instanceof Player &&
			!isAspectKnown((EntityLiving) owner, AspectRottenness.class) &&
			getRottenness() > 7
		) {
			observeAspect((EntityLiving) owner, AspectRottenness.class);
			
			owner.getDungeon().log("Something in your inventory really stinks...");
		}
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += getEatenState() == EatenState.PARTLY_EATEN ? "partly eaten " : "";
		
		if (getRottenness() > 7 && isAspectKnown(observer, AspectRottenness.class)) {
			s += "rotten ";
		}
		
		s += entity.getName(observer, requiresCapitalisation) +
			" corpse" + (plural ? "s" : "");
		
		return s;
	}
	
	@Override
	public float getWeight() {
		if (entity instanceof Monster) {
			return ((Monster) entity).getWeight();
		} else {
			return 250;
		}
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_CORPSE;
	}
	
	@Override
	public int getNutrition() {
		if (entity instanceof Monster) {
			return ((Monster) entity).getNutrition();
		} else {
			return 0;
		}
	}
	
	public EntityLiving getEntity() {
		return entity;
	}
	
	@Override
	public int getTurnsRequiredToEat() {
		if (entity instanceof Monster) {
			return ((Monster) entity).getWeight() / 64 + 3;
		} else {
			return entity.getSize() == EntityLiving.Size.LARGE ? 5 : 4;
		}
	}
	
	@Override
	public List<StatusEffect> getStatusEffects(EntityLiving victim) {
		List<StatusEffect> effects = new ArrayList<>();
		
		if (entity instanceof Monster) {
			Monster monster = (Monster) entity;
			
			if (monster.getCorpseEffects(victim) != null) {
				effects.addAll(monster.getCorpseEffects(victim));
			}
			
			if (getRottenness() > 7) {
				effects.add(new FoodPoisoning(entity.getDungeon(), entity));
			}
		}
		
		return effects;
	}
	
	public int getRottenness() {
		if (entity instanceof Monster) {
			Monster monster = (Monster) entity;
			
			if (monster.shouldCorpsesRot()) {
				AtomicInteger rottenness = new AtomicInteger(getAge() / 15);
				
				getAspect(AspectBeatitude.class).ifPresent(a -> {
					AspectBeatitude ab = (AspectBeatitude) a;
					
					switch (ab.getBeatitude()) {
						case BLESSED:
							rottenness.addAndGet(-2);
							break;
						case CURSED:
							rottenness.addAndGet(2);
							break;
					}
				});
				
				return Math.max(rottenness.get(), 0);
			}
		}
		
		return 0;
	}
	
	@Override
	public boolean shouldStack() {
		return false;
	}
	
	@Override
	public boolean equals(Item other) {
		if (other instanceof ItemCorpse) {
			return super.equals(other) && ((ItemCorpse) other).getEntity().getClass() == entity.getClass();
		}
		
		return super.equals(other);
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		JSONObject serialisedEntity = new JSONObject();
		entity.serialise(serialisedEntity);
		
		obj.put("entity", serialisedEntity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		JSONObject serialisedEntity = obj.getJSONObject("entity");
		
		String entityClassName = serialisedEntity.getString("class");
		int x = serialisedEntity.getInt("x");
		int y = serialisedEntity.getInt("y");
		
		try {
			Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(entityClassName);
			Constructor<? extends Entity> entityConstructor = entityClass.getConstructor(
				Dungeon.class,
				Level.class,
				int.class,
				int.class
			);
			
			entity = (EntityLiving) entityConstructor.newInstance(null, null, x, y);
			entity.unserialise(serialisedEntity);
		} catch (ClassNotFoundException e) {
			ErrorHandler.error("Unknown entity class " + entityClassName, e);
		} catch (NoSuchMethodException e) {
			ErrorHandler.error("Entity class has no unserialisation constructor " + entityClassName, e);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			ErrorHandler.error("Error loading entity class " + entityClassName, e);
		}
	}
}
