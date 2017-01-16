package pw.lemmmy.jrogue.dungeon.items.identity;

import org.json.JSONObject;

public class AspectGemValue extends Aspect {
	@Override
	public void serialise(JSONObject obj) {
		
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		
	}
	
	@Override
	public String getName() {
		return "Gem value";
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
}
