package pw.lemmmy.jrogue.dungeon.entities;

public interface Extinguishable {
	void light();
	
	void extinguish();
	
	boolean isLit();
	
	void setLit(boolean lit);
}
