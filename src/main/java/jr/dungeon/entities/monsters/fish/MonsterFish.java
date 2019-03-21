package jr.dungeon.entities.monsters.fish;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.FishAI;
import jr.dungeon.events.EventHandler;
import jr.language.Lexicon;
import jr.language.Noun;
import org.json.JSONObject;

import java.util.List;

public class MonsterFish extends Monster {
	@Expose private FishColour colour;
	
	public MonsterFish(Dungeon dungeon, Level level, int x, int y) { // unserialisation constructor
		super(dungeon, level, x, y);
	}
	
	public MonsterFish(Dungeon dungeon, Level level, int x, int y, FishColour colour) {
		super(dungeon, level, x, y, 1);
		
		this.colour = colour;
		
		setAI(new FishAI(this));
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.fish.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		switch (colour) {
			case RED:
				return EntityAppearance.APPEARANCE_FISH_RED;
			case ORANGE:
				return EntityAppearance.APPEARANCE_FISH_ORANGE;
			case YELLOW:
				return EntityAppearance.APPEARANCE_FISH_YELLOW;
			case GREEN:
				return EntityAppearance.APPEARANCE_FISH_GREEN;
			case BLUE:
				return EntityAppearance.APPEARANCE_FISH_BLUE;
			case PURPLE:
				return EntityAppearance.APPEARANCE_FISH_PURPLE;
			default:
				return EntityAppearance.APPEARANCE_FISH_BLUE;
		}
	}
	
	@Override
	public Size getSize() {
		return Size.SMALL;
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED;
	}
	
	@Override
	public boolean isHostile() {
		return false;
	}
	
	@Override
	public int getWeight() {
		return 50;
	}
	
	@Override
	public int getNutritionalValue() {
		return 50;
	}
	
	@Override
	public float getCorpseChance() {
		return 0.0f;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 10;
	}
	
	@EventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		getDungeon().logRandom("Bloop.", "Glug.", "Splash!", "Sploosh!");
	}
	
	@Override
	public int getVisibilityRange() {
		return 2;
	}
	
	@Override
	public boolean canMoveDiagonally() {
		return true;
	}
	
	@Override
	public boolean canMeleeAttack() {
		return false;
	}
	
	@Override
	public boolean canRangedAttack() {
		return false;
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@Override
	public int getExperienceRewarded() {
		return 0;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("colour", colour.name());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		colour = FishColour.valueOf(obj.getString("colour"));
	}
	
	public enum FishColour {
		RED, YELLOW, ORANGE, GREEN, BLUE, PURPLE
	}
}
