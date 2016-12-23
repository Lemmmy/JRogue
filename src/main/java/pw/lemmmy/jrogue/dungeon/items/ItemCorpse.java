package pw.lemmmy.jrogue.dungeon.items;

import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.Monster;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return (getEatenState() == EatenState.PARTLY_EATEN ? "partly eaten " : "") +
			entity.getName(requiresCapitalisation) +
			" corpse" + (plural ? "s" : "");
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

	@Override
	public int getTurnsRequiredToEat() {
		return entity.getSize() == LivingEntity.Size.LARGE ? 3 : 2;
	}

	@Override
	public List<StatusEffect> getStatusEffects(LivingEntity victim) {
		if (entity instanceof Monster) {
			return ((Monster) entity).getCorpseEffects(victim);
		} else {
			return null;
		}
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
