package pw.lemmmy.jrogue.utils;

import com.badlogic.gdx.graphics.Color;

public class Gradient {
	private Color start;
	private Color end;
	
	public Gradient(Color start, Color end) {
		this.start = start;
		this.end = end;
	}
	
	public Color getColourAtPoint(float point) {
		float r = start.r * sigmoid(point) + end.r * (1 - sigmoid(point));
		float g = start.g * sigmoid(point) + end.g * (1 - sigmoid(point));
		float b = start.b * sigmoid(point) + end.b * (1 - sigmoid(point));
		float a = start.a * sigmoid(point) + end.a * (1 - sigmoid(point));
		
		return new Color(r, g, b, a);
	}
	
	private static float sigmoid(float x) {
		return (float) (1 / (1 + Math.pow(Math.E, (-1 * x))));
	}
}
