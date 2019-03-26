package jr.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * A class representing a colour.
 */
@JsonAdapter(Colour.ColourTypeAdapter.class)
public class Colour {
	/** Pure white (0xFFFFFFFF). */
	public static final Colour WHITE = new Colour(0xFFFFFFFF);
	/** Pure black (0x000000FF). */
	public static final Colour BLACK = new Colour(0x000000FF);
	
	public float r, g, b, a;

	/**
	 * A copy constructor. Creates a {@link Colour} that matches the given {@link Colour}.
	 *
	 * @param colour The colour to copy.
	 */
	public Colour(Colour colour) {
		set(colour);
	}

	/**
	 * Constructs a Colour from RGBA components ranging from 0.0f to 1.0f.
	 *
	 * @param r The red component (0.0 - 1.0).
	 * @param g The green component (0.0 - 1.0).
	 * @param b The blue component (0.0 - 1.0).
	 * @param a The alpha component (0.0 - 1.0).
	 */
	public Colour(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
		clamp();
	}

	/**
	 * Constructs a Colour from RGBA components ranging from 0 to 255.
	 *
	 * @param r The red component (0 - 255).
	 * @param g The green component (0 - 255).
	 * @param b The blue component (0 - 255).
	 * @param a The alpha component (0 - 255).
	 */
	public Colour(int r, int g, int b, int a) {
		this.r = (float) r / 255f;
		this.g = (float) g / 255f;
		this.b = (float) b / 255f;
		this.a = (float) a / 255f;
		
		clamp();
	}

	/**
	 * Constructs a Colour from an RGBA8888 bitfield.
	 *
	 * @param rgba8888 The colour bitfield in the format {@code 0xRRGGBBAA}.
	 */
	public Colour(int rgba8888) {
		rgba8888ToColour(this, rgba8888);
	}

	/**
	 * Copies the given colour's attributes into the colour.
	 *
	 * @param colour The colour to copy.
	 * @return {@code this}, can be used for method chaining.
	 */
	public Colour set(Colour colour) {
		this.r = colour.r;
		this.g = colour.g;
		this.b = colour.b;
		this.a = colour.a;
		
		return this;
	}
	
	public Colour mul(Colour colour) {
		this.r *= colour.r;
		this.g *= colour.g;
		this.b *= colour.b;
		this.a *= colour.a;
		
		return clamp();
	}
	
	public Colour mul(float value) {
		this.r *= value;
		this.g *= value;
		this.b *= value;
		this.a *= value;
		
		return clamp();
	}
	
	public Colour add(Colour colour) {
		this.r += colour.r;
		this.g += colour.g;
		this.b += colour.b;
		this.a += colour.a;
		
		return clamp();
	}
	
	public Colour sub(Colour colour) {
		this.r -= colour.r;
		this.g -= colour.g;
		this.b -= colour.b;
		this.a -= colour.a;
		
		return clamp();
	}

	/**
	 * Ensures no colour component is out of range.
	 *
	 * @return {@code this}, can be used for method chaining.
	 */
	public Colour clamp() {
		if (r < 0) { r = 0; } else if (r > 1) { r = 1; }
		if (g < 0) { g = 0; } else if (g > 1) { g = 1; }
		if (b < 0) { b = 0; } else if (b > 1) { b = 1; }
		if (a < 0) { a = 0; } else if (a > 1) { a = 1; }
		
		return this;
	}
	
	public Colour set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
		return clamp();
	}
	
	public Colour set(int r, int g, int b, int a) {
		this.r = (float) r / 255f;
		this.g = (float) g / 255f;
		this.b = (float) b / 255f;
		this.a = (float) a / 255f;
		
		return clamp();
	}
	
	public Colour set(int rgba) {
		rgba8888ToColour(this, rgba);
		
		return this;
	}
	
	public Colour add(float r, float g, float b, float a) {
		this.r += r;
		this.g += g;
		this.b += b;
		this.a += a;
		
		return clamp();
	}
	
	public Colour sub(float r, float g, float b, float a) {
		this.r -= r;
		this.g -= g;
		this.b -= b;
		this.a -= a;
		
		return clamp();
	}
	
	public Colour mul(float r, float g, float b, float a) {
		this.r *= r;
		this.g *= g;
		this.b *= b;
		this.a *= a;
		
		return clamp();
	}
	
	public Colour lerp(final Colour target, final float t) {
		this.r += t * (target.r - this.r);
		this.g += t * (target.g - this.g);
		this.b += t * (target.b - this.b);
		this.a += t * (target.a - this.a);
		
		return clamp();
	}
	
	public Colour lerp(final float r, final float g, final float b, final float a, final float t) {
		this.r += t * (r - this.r);
		this.g += t * (g - this.g);
		this.b += t * (b - this.b);
		this.a += t * (a - this.a);
		
		return clamp();
	}
	
	public Colour premultiplyAlpha() {
		r *= a;
		g *= a;
		b *= a;
		
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		Colour colour = (Colour) o;
		
		return toIntBits() == colour.toIntBits();
	}
	
	@Override
	public int hashCode() {
		int result = r != +0.0f ? Float.floatToIntBits(r) : 0;
		result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
		result = 31 * result + (b != +0.0f ? Float.floatToIntBits(b) : 0);
		result = 31 * result + (a != +0.0f ? Float.floatToIntBits(a) : 0);
		
		return result;
	}
	
	public float toFloatBits() {
		int colour = (int) (255 * r) << 24 | (int) (255 * g) << 16 | (int) (255 * b) << 8 | (int) (255 * a);
		
		return Float.intBitsToFloat(colour & 0xfeffffff);
	}
	
	public int toIntBits() {
		return  (int) (255 * r) << 24 | (int) (255 * g) << 16 | (int) (255 * b) << 8 | (int) (255 * a);
	}
	
	public String toString() {
		StringBuilder value = new StringBuilder(Integer
			.toHexString((int) (255 * r) << 24 | (int) (255 * g) << 16 | (int) (255 * b) << 8 | (int) (255 * a)));
		
		while (value.length() < 8) {
			value.insert(0, "0");
		}
		
		return value.toString();
	}
	
	public static Colour valueOf(String hex) {
		hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;
		
		int r = Integer.valueOf(hex.substring(0, 2), 16);
		int g = Integer.valueOf(hex.substring(2, 4), 16);
		int b = Integer.valueOf(hex.substring(4, 6), 16);
		int a = hex.length() != 8 ? 255 : Integer.valueOf(hex.substring(6, 8), 16);
		
		return new Colour(r / 255f, g / 255f, b / 255f, a / 255f);
	}
	
	public static float toFloatBits(int r, int g, int b, int a) {
		int colour = a << 24 | b << 16 | g << 8 | r;
		
		return Float.intBitsToFloat(colour & 0xfeffffff);
	}
	
	public static float toFloatBits(float r, float g, float b, float a) {
		int colour = (int) (255 * a) << 24 | (int) (255 * b) << 16 | (int) (255 * g) << 8 | (int) (255 * r);
		
		return Float.intBitsToFloat(colour & 0xfeffffff);
	}
	
	public static int toIntBits(int r, int g, int b, int a) {
		return a << 24 | b << 16 | g << 8 | r;
	}
	
	public static int alpha(float alpha) {
		return (int) (alpha * 255.0f);
	}
	
	public static int luminanceAlpha(float luminance, float alpha) {
		return (int) (luminance * 255.0f) << 8 | (int) (alpha * 255);
	}
	
	public static int rgb565(float r, float g, float b) {
		return (int) (r * 31) << 11 | (int) (g * 63) << 5 | (int) (b * 31);
	}
	
	public static int rgba4444(float r, float g, float b, float a) {
		return (int) (r * 15) << 12 | (int) (g * 15) << 8 | (int) (b * 15) << 4 | (int) (a * 15);
	}
	
	public static int rgb888(float r, float g, float b) {
		return (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
	}
	
	public static int rgba8888(float r, float g, float b, float a) {
		return (int) (r * 255) << 24 | (int) (g * 255) << 16 | (int) (b * 255) << 8 | (int) (a * 255);
	}
	
	public static int argb8888(float a, float r, float g, float b) {
		return (int) (a * 255) << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
	}
	
	public static int rgb565(Colour colour) {
		return (int) (colour.r * 31) << 11 | (int) (colour.g * 63) << 5 | (int) (colour.b * 31);
	}
	
	public static int rgba4444(Colour colour) {
		return (int) (colour.r * 15) << 12 | (int) (colour.g * 15) << 8 | (int) (colour.b * 15) << 4 | (int) (colour.a * 15);
	}
	
	public static int rgb888(Colour colour) {
		return (int) (colour.r * 255) << 16 | (int) (colour.g * 255) << 8 | (int) (colour.b * 255);
	}
	
	public static int rgba8888(Colour colour) {
		return (int) (colour.r * 255) << 24 | (int) (colour.g * 255) << 16 | (int) (colour.b * 255) << 8 | (int) (colour.a * 255);
	}
	
	public static int argb8888(Colour colour) {
		return (int) (colour.a * 255) << 24 | (int) (colour.r * 255) << 16 | (int) (colour.g * 255) << 8 | (int) (colour.b * 255);
	}
	
	public static void rgb565ToColour(Colour colour, int value) {
		colour.r = ((value & 0x0000F800) >>> 11) / 31f;
		colour.g = ((value & 0x000007E0) >>> 5) / 63f;
		colour.b = (value & 0x0000001F) / 31f;
	}
	
	public static void rgba4444ToColour(Colour colour, int value) {
		colour.r = ((value & 0x0000f000) >>> 12) / 15f;
		colour.g = ((value & 0x00000f00) >>> 8) / 15f;
		colour.b = ((value & 0x000000f0) >>> 4) / 15f;
		colour.a = (value & 0x0000000f) / 15f;
	}
	
	public static void rgb888ToColour(Colour colour, int value) {
		colour.r = ((value & 0x00ff0000) >>> 16) / 255f;
		colour.g = ((value & 0x0000ff00) >>> 8) / 255f;
		colour.b = (value & 0x000000ff) / 255f;
	}
	
	public static void rgba8888ToColour(Colour colour, int value) {
		colour.r = ((value & 0xff000000) >>> 24) / 255f;
		colour.g = ((value & 0x00ff0000) >>> 16) / 255f;
		colour.b = ((value & 0x0000ff00) >>> 8) / 255f;
		colour.a = (value & 0x000000ff) / 255f;
	}
	
	public static void argb8888ToColour(Colour colour, int value) {
		colour.a = ((value & 0xff000000) >>> 24) / 255f;
		colour.r = ((value & 0x00ff0000) >>> 16) / 255f;
		colour.g = ((value & 0x0000ff00) >>> 8) / 255f;
		colour.b = (value & 0x000000ff) / 255f;
	}
	
	public static void abgr8888ToColour(Colour colour, float value) {
		int c = Float.floatToRawIntBits(value);
		
		colour.a = ((c & 0xff000000) >>> 24) / 255f;
		colour.b = ((c & 0x00ff0000) >>> 16) / 255f;
		colour.g = ((c & 0x0000ff00) >>> 8) / 255f;
		colour.r = (c & 0x000000ff) / 255f;
	}
	
	public Colour copy() {
		return new Colour(this);
	}
	
	public class ColourTypeAdapter extends TypeAdapter<Colour> {
		@Override
		public void write(JsonWriter out, Colour value) throws IOException {
			out.value(value.toIntBits());
		}
		
		@Override
		public Colour read(JsonReader in) throws IOException {
			return new Colour(in.nextInt());
		}
	}
}
