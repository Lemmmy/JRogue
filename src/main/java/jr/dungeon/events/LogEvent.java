package jr.dungeon.events;

public class LogEvent extends DungeonEvent {
	private String entry;
	
	public LogEvent(String entry) {
		this.entry = entry;
	}
	
	public String getEntry() {
		return entry;
	}
}
