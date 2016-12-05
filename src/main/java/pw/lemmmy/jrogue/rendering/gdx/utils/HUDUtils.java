package pw.lemmmy.jrogue.rendering.gdx.utils;

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
    
	public static String replaceMarkupString(String s) {
		s = s.replace("[GREEN]", "[P_GREEN_3]");
		s = s.replace("[CYAN]", "[P_CYAN_1]");
		s = s.replace("[BLUE]", "[P_BLUE_1]");
		s = s.replace("[YELLOW]", "[P_YELLOW]");

		return s;
	}
}
