package jr.dungeon.entities.player;

import lombok.Getter;

@Getter
public enum NutritionState {
    CHOKING("Choking", 2),
    STUFFED("Stuffed", 1),
    NOT_HUNGRY("Not hungry"),
    HUNGRY("Hungry", 1),
    STARVING("Starving", 2),
    FAINTING("Fainting", 2);
    
    private String string;
    private int importance;
    
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
    
    public static NutritionState fromNutrition(float nutrition) {
        if (nutrition >= 2000) {
            return NutritionState.CHOKING;
        } else if (nutrition >= 1500) {
            return NutritionState.STUFFED;
        } else if (nutrition >= 750) {
            return NutritionState.NOT_HUNGRY;
        } else if (nutrition >= 250) {
            return NutritionState.HUNGRY;
        } else if (nutrition >= 0) {
            return NutritionState.STARVING;
        } else {
            return NutritionState.FAINTING;
        }
    }
}
