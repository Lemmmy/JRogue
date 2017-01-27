package jr.dungeon.events;

public abstract class DungeonEvent {
	private boolean cancelled;
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public boolean isSelf(Object other) {
		return false;
	}
}
