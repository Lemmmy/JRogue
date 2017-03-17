package jr.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;

/**
 * Simple pooled two-point Gradient class. Takes a start and end colour, and lets you get the colour from any point in
 * the gradient. Uses GDX colours.
 */
public class Gradient implements Pool.Poolable {
	/**
	 * The pool of gradients.
	 */
	private static final Pool<Gradient> gradientPool = new Pool<Gradient>() {
		@Override
		protected Gradient newObject() {
			return new Gradient();
		}
	};
	
	/**
	 * The start colour of the gradient.
	 */
	private Color start;
	/**
	 * The last interpolated colour.
	 */
	private Color lastInterpolated;
	/**
	 * The end colour of the gradient.
	 */
	private Color end;
	
	/**
	 * Do not instantiate manually! Use {@link Gradient#getGradient(Color, Color)} instead.
	 */
	private Gradient() {
		reset();
	}
	
	/**
	 * Gets a new gradient from the pool.
	 *
	 * @param start The start colour of the gradient.
	 * @param end The end colour of the gradient.
	 *
	 * @return The new Gradient instance.
	 */
	public static Gradient getGradient(Color start, Color end) {
		Gradient g = gradientPool.obtain();
		g.init(start, end);
		return g;
	}
	
	/**
	 * Frees a gradient from the pool.
	 *
	 * @param gradient The gradient to free.
	 */
	public static void free(Gradient gradient) {
		gradientPool.free(gradient);
	}

	private void init(Color start, Color end) {
		this.start = start;
		this.lastInterpolated = new Color();
		this.end = end;
	}
	
	/**
	 * @param point The percentage of the gradient (from 0 to 1) to get the colour from.
	 *
	 * @return The interpolated colour.
	 */
	public Color getColourAtPoint(float point) {
		float r = start.r * sigmoid(point) + end.r * (1 - sigmoid(point));
		float g = start.g * sigmoid(point) + end.g * (1 - sigmoid(point));
		float b = start.b * sigmoid(point) + end.b * (1 - sigmoid(point));
		float a = start.a * sigmoid(point) + end.a * (1 - sigmoid(point));
		
		lastInterpolated.set(r, g, b, a);
		return lastInterpolated;
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
