package pw.lemmmy.jrogue.dungeon.items.identity;

import org.json.JSONObject;

public class AspectBeatitude extends Aspect {
	private Beatitude beatitude = Beatitude.UNCURSED;
	
	public Beatitude getBeatitude() {
		return beatitude;
	}
	
	public void setBeatitude(Beatitude beatitude) {
		this.beatitude = beatitude;
	}
	
	@Override
	public String getName() {
		return "Beatitude";
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		obj.put("beatitude", beatitude.name());
	}
	
	@Override
	public void unserialise(JSONObject obj) {
		beatitude = Beatitude.valueOf(obj.optString("beatitude", Beatitude.UNCURSED.name()));
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		
		AspectBeatitude that = (AspectBeatitude) o;
		
		return beatitude == that.beatitude;
	}
	
	@Override
	public int hashCode() {
		return beatitude != null ? beatitude.hashCode() : 0;
	}
	
	public enum Beatitude {
		BLESSED,
		UNCURSED,
		CURSED
	}
}
