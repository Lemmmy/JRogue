package jr.utils;

public class Easing {
	public static float easeIn(float time, float start, float change, float duration) {
		return change * (time /= duration) * time * time + start;
	}
	
	public static float easeOut(float time, float start, float change, float duration) {
		return change * ((time = time / duration - 1) * time * time + 1) + start;
	}
	
	public static float easeInOut(float time, float start, float change, float duration) {
		if ((time /= duration / 2) < 1) return change / 2 * time * time * time + start;
		return change / 2 * ((time -= 2) * time * time + 2) + start;
	}
}
