package pw.lemmmy.jrogue.dungeon.items.comestibles;

import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.FoodPoisoning;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.identity.AspectBeatitude;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemCorpse extends ItemComestible {
	private LivingEntity entity;
	
	public ItemCorpse() { // unserialisation constructor
		super();
	}
	
	public ItemCorpse(LivingEntity entity) {
		super();
		
		this.entity = entity;
	}
	
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += (getEatenState() == EatenState.PARTLY_EATEN ? "partly eaten " : "") +
			entity.getName(observer, requiresCapitalisation) +
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
	
	public LivingEntity getEntity() {
		return entity;
	}
	
	@Override
	public int getTurnsRequiredToEat() {
		if (entity instanceof Monster) {
			return ((Monster) entity).getWeight() / 64 + 3;
		} else {
			return entity.getSize() == LivingEntity.Size.LARGE ? 5 : 4;
		}
	}
	
	@Override
	public List<StatusEffect> getStatusEffects(LivingEntity victim) {
		List<StatusEffect> effects = new ArrayList<>();
		
		if (entity instanceof Monster) {
			Monster monster = (Monster) entity;
			
			if (monster.getCorpseEffects(victim) != null) {
				effects.addAll(monster.getCorpseEffects(victim));
			}
			
			if (getRottenness() > 6) {
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
			
			entity = (LivingEntity) entityConstructor.newInstance(null, null, x, y);
			entity.unserialise(serialisedEntity);
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown entity class {}", entityClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Entity class {} has no unserialisation constructor", entityClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading entity class {}", entityClassName);
			JRogue.getLogger().error(e);
		}
	}
}
