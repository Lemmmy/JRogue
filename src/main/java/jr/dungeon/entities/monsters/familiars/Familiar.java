package jr.dungeon.entities.monsters.familiars;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityChangeLevelEvent;
import jr.dungeon.entities.events.EntityDeathEvent;
import jr.dungeon.entities.interfaces.Friendly;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.NutritionState;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventPriority;
import jr.language.LanguageUtils;
import jr.language.Noun;
import jr.language.transformations.Capitalise;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * An {@link jr.dungeon.entities.Entity} tamed by the {@link jr.dungeon.entities.player.Player}. A Player's companion
 * in the dungeon.
 */
public abstract class Familiar extends Monster implements Friendly {
	/**
	 * The familiar's age from 0 to 2. 0 is youngest, 2 is oldest.
	 */
	@Getter private int age;
	
	private String name;
	
	@Getter @Setter
	private int nutrition;
	@Getter private NutritionState lastNutritionState;
	
	public Familiar(Dungeon dungeon, Level level, int x, int y) { // unserialiastion constructor
		super(dungeon, level, x, y);
	}
	
	@Override
	public int getNutritionalValue() {
		return getWeight();
	}
	
	public NutritionState getNutritionState() {
		return NutritionState.fromNutrition(nutrition);
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public float getCorpseChance() {
		return 0; // what the hell
	}
	
	@Override
	public boolean isHostile() {
		return false;
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true; // why did i write this method
	}
	
	public abstract Noun getDefaultName(EntityLiving observer);
	
	@Override
	public Noun getName(EntityLiving observer) {
		if (name != null) {
			return new Noun(name).addTransformer(Capitalise.class, Capitalise.first);
		} else {
			return LanguageUtils.subjectPossessive(this);
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		updateNutrition();
	}
	
	private void updateNutrition() {
		if (getNutritionState() != lastNutritionState) {
			lastNutritionState = getNutritionState();
		}
		
		if (getNutritionState() == NutritionState.CHOKING) {
			damage(new DamageSource(this, null, DamageType.CHOKING), 1);
		}
		
		if (getNutritionState() == NutritionState.STARVING) {
			damage(new DamageSource(this, null, DamageType.STARVING), 1);
		}
		
		nutrition--;
	}
	
	@EventHandler
	public void onPlayerChangeLevel(EntityChangeLevelEvent e) {
		if (!e.isEntityPlayer()) return;
		
		if (
			e.getSrc().getLevel().equals(getLevel()) &&
			e.getSrc().isAdjacentTo(getPosition())
		) {
			setLevel(e.getDest().getLevel());
			setPositionFresh(e.getDest().getPosition());
			getAI().suppress(2);
		}
	}
	
	@EventHandler(selfOnly = true, priority = EventPriority.LOWEST)
	public void onDie(EntityDeathEvent e) {
		getDungeon().You("feel sad for a moment...");
		
		if (e.isAttackerPlayer()) {
			// TODO: cripple player's luck and god relationship. they are a terrible person
		}
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("age", age);
		obj.put("nutrition", nutrition);
		obj.put("name", name);
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);

		age = obj.getInt("age");
		nutrition = obj.getInt("nutrition");
		try { name = obj.getString("name"); } catch (JSONException ignored) {}
	}
}
