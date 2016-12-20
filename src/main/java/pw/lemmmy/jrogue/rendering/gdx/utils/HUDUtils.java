package pw.lemmmy.jrogue.rendering.gdx.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import pw.lemmmy.jrogue.dungeon.entities.Player;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

public class HUDUtils {
	public static String getHealthColour(int health, int maxHealth) {
		if (health <= maxHealth / 5) {
			return "P_RED";
		} else if (health <= maxHealth / 3) {
			return "P_ORANGE_3";
		} else if (health <= maxHealth / 2) {
			return "P_YELLOW";
		} else {
			return "P_GREEN_3";
		}
	}

	public static Color getNutritionColour(Player.NutritionState nutritionState) {
		switch (nutritionState.getImportance()) {
			case 1:
				return Colors.get("P_YELLOW");
			case 2:
				return Colors.get("P_RED");
			default:
				return Color.WHITE;
		}
	}

	public static String getStatusEffectColour(StatusEffect.Severity severity) {
		switch (severity) {
			case MINOR:
				return "P_YELLOW";
			case MAJOR:
				return "P_ORANGE_2";
			case CRITICAL:
				return "P_RED";
			default:
				return "WHITE";
		}
	}

	public static String replaceMarkupString(String s) {
		s = s.replace("[RED]", "[P_RED]");
		s = s.replace("[ORANGE]", "[P_ORANGE_2]");
		s = s.replace("[YELLOW]", "[P_YELLOW]");
		s = s.replace("[GREEN]", "[P_GREEN_3]");
		s = s.replace("[CYAN]", "[P_CYAN_1]");
		s = s.replace("[BLUE]", "[P_BLUE_1]");

		return s;
	}
}
