package pw.lemmmy.jrogue.dungeon.entities;

public interface Extinguishable {
	default void light() {
		setLit(true);
	}
	
	default void extinguish() {
		setLit(false);
	}
	
	boolean isLit();
	
	void setLit(boolean lit);
}
