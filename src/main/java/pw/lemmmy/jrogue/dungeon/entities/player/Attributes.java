package pw.lemmmy.jrogue.dungeon.entities.player;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.EnumMap;

public class Attributes {
	private static final int MAX_ATTRIBUTE_LEVEL = 30;
	
	private EnumMap<Attribute, Integer> attributes = new EnumMap<>(Attribute.class);
	
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
	
	public void incrementAttribute(Attribute attribute, Player player) {
		if (!canIncrementAttribute(attribute, player)) {
			return;
		}
		
		player.decrementSpendableSkillPoints();
		setAttribute(attribute, getAttribute(attribute) + 1);
	}
	
	public boolean canIncrementAttribute(Attribute attribute, Player player) {
		return player.getSpendableSkillPoints() > 0 && getAttribute(attribute) < 30;
	}
	
	public void serialise(JSONObject obj) {
		attributes.forEach((attribute, level) -> {
			String keyName = "attribute" + attribute.getName();
			
			obj.put(keyName, level);
		});
	}
	
	public void unserialise(JSONObject obj) {
		Arrays.stream(Attribute.values()).forEach(attribute -> {
			String keyName = "attribute" + attribute.getName();
			
			if (obj.has(keyName)) {
				attributes.put(attribute, obj.getInt(keyName));
			}
		});
	}
}
