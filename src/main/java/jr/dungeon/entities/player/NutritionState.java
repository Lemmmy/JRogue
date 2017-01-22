package jr.dungeon.entities.player;

public enum NutritionState {
	CHOKING("Choking", 2),
	STUFFED("Stuffed", 1),
	NOT_HUNGRY("Not hungry"),
	HUNGRY("Hungry", 1),
	STARVING("Starving", 2),
	FAINTING("Fainting", 2);
	
	private String string;
	private int importance = 0;
	
	NutritionState(String string) {
		this(string, 0);
	}
	
	NutritionState(String string, int importance) {
		this.string = string;
		this.importance = importance;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	public int getImportance() {
		return importance;
	}
}
