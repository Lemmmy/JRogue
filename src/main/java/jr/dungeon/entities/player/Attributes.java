package jr.dungeon.entities.player;

import com.google.gson.annotations.Expose;
import jr.dungeon.entities.events.EntityLevelledUpEvent;
import jr.dungeon.events.EventHandler;
import jr.dungeon.events.EventListener;
import jr.dungeon.events.GameStartedEvent;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Attributes implements EventListener {
	private static final int MAX_ATTRIBUTE_LEVEL = 30;
	
	@Expose private Map<Attribute, Integer> attributes = new HashMap<>();
	private Map<Attribute, Integer> defaults = new HashMap<>();
	@Expose @Getter private int spendableSkillPoints = 3;
	
	public Attributes() {
		clear();
	}
	
	public void clear() {
		spendableSkillPoints = 3;
		Arrays.stream(Attribute.values()).forEach(a -> { attributes.put(a, 0); defaults.put(a, 0); });
	}
	
	public Map<Attribute, Integer> getAttributeMap() {
		return attributes;
	}
	
	public int getAttribute(Attribute attribute) {
		return attributes.getOrDefault(attribute, 0);
	}
	
	public void setAttribute(Attribute attribute, int level) {
		attributes.put(attribute, level);
	}
	
	public void initialiseAttribute(Attribute attribute, int level) {
		attributes.put(attribute, level);
		defaults.put(attribute, level);
	}
	
	public void decrementAttribute(Attribute attribute) {
		if (!canDecrementAttribute(attribute)) {
			return;
		}
		
		spendableSkillPoints++;
		setAttribute(attribute, getAttribute(attribute) - 1);
	}
	
	public boolean canDecrementAttribute(Attribute attribute) {
		return getAttribute(attribute) > defaults.get(attribute);
	}
	
	public void incrementAttribute(Attribute attribute) {
		if (!canIncrementAttribute(attribute)) {
			return;
		}
		
		spendableSkillPoints = Math.max(0, spendableSkillPoints - 1);
		setAttribute(attribute, getAttribute(attribute) + 1);
	}
	
	public boolean canIncrementAttribute(Attribute attribute) {
		return spendableSkillPoints > 0 && getAttribute(attribute) < 30;
	}
	
	@EventHandler
	private void onGameStarted(GameStartedEvent event) {
		if (spendableSkillPoints > 0) {
			event.getDungeon().greenYou(
				"have %,d spendable skill point%s.",
				spendableSkillPoints,
				spendableSkillPoints == 1 ? "" : "s"
			);
		}
	}
	
	@EventHandler()
	public void onLevelUp(EntityLevelledUpEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		event.getDungeon().greenYou(
			"have %,d spendable skill point%s.",
			++spendableSkillPoints,
			spendableSkillPoints == 1 ? "" : "s"
		);
	}
}
