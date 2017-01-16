package pw.lemmmy.jrogue.dungeon.items.identity;

import org.json.JSONObject;

public class AspectBookContents extends Aspect {
	@Override
	public String getName() {
		return "Book contents";
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		
	}
	
}
