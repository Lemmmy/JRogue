package jr.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;

public class Gradient implements Pool.Poolable {
	private static final Pool<Gradient> gradientPool = new Pool<Gradient>() {
		@Override
		protected Gradient newObject() {
			return new Gradient();
		}
	};

	private Color start;
	private Color end;

	private Gradient() {
		reset();
	}

	public static Gradient getGradient(Color start, Color end) {
		Gradient g = gradientPool.obtain();
		g.init(start, end);
		return g;
	}

	public static void free(Gradient gradient) {
		gradientPool.free(gradient);
	}

	private void init(Color start, Color end) {
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
		return (float) (1 / (1 + Math.pow(Math.E, -1 * x)));
	}

	@Override
	public void reset() {
		this.start = Color.BLACK;
		this.end = Color.BLACK;
	}
}
