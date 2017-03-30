package jr.dungeon.entities.player;

import jr.dungeon.entities.events.EntityLevelledUpEvent;
import jr.dungeon.events.DungeonEventHandler;
import jr.dungeon.events.DungeonEventListener;
import jr.dungeon.events.GameStartedEvent;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.EnumMap;

public class Attributes implements DungeonEventListener {
	private static final int MAX_ATTRIBUTE_LEVEL = 30;
	
	private EnumMap<Attribute, Integer> attributes = new EnumMap<>(Attribute.class);
	@Getter private int spendableSkillPoints = 3;
	
	public Attributes() {
		Arrays.stream(Attribute.values()).forEach(a -> attributes.put(a, 0));
	}
	
	public EnumMap<Attribute, Integer> getAttributeMap() {
		return attributes;
	}
	
	public int getAttribute(Attribute attribute) {
		if (attributes.containsKey(attribute)) {
			return attributes.get(attribute);
		} else {
			return 0;
		}
	}
	
	public void setAttribute(Attribute attribute, int level) {
		attributes.put(attribute, level);
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
	
	public void serialise(JSONObject obj) {
		obj.put("spendableSkillPoints", spendableSkillPoints);
		
		attributes.forEach((attribute, level) -> {
			String keyName = "attribute" + attribute.getName();
			
			obj.put(keyName, level);
		});
	}
	
	public void unserialise(JSONObject obj) {
		spendableSkillPoints = obj.getInt("spendableSkillPoints");
		
		Arrays.stream(Attribute.values()).forEach(attribute -> {
			String keyName = "attribute" + attribute.getName();
			
			if (obj.has(keyName)) {
				attributes.put(attribute, obj.getInt(keyName));
			}
		});
	}
	
	
	@DungeonEventHandler
	private void onGameStarted(GameStartedEvent event) {
		if (spendableSkillPoints > 0) {
			event.getDungeon().greenYou(
				"have %,d spendable skill point%s.",
				spendableSkillPoints,
				spendableSkillPoints == 1 ? "" : "s"
			);
		}
	}
	
	@DungeonEventHandler()
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
