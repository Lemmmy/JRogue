package jr.dungeon.events;

public class BeforeTurnEvent extends DungeonEvent {
	private long turn;
	
	public BeforeTurnEvent(long turn) {
		this.turn = turn;
	}
	
	public long getTurn() {
		return turn;
	}
}
