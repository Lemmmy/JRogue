package jr.dungeon.events;

public class TurnEvent extends DungeonEvent {
	private long turn;
	
	public TurnEvent(long turn) {
		this.turn = turn;
	}
	
	public long getTurn() {
		return turn;
	}
}
