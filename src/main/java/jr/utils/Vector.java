package jr.utils;

import lombok.Data;

@Data
public class Vector {
	public static Vector ZERO = new Vector(0, 0);

	private final float x, y;

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector set(float x, float y) {
		return new Vector(this.x + x, this.y + y);
	}

	public Vector add(float x, float y) {
		return new Vector(this.x + x, this.y + y);
	}

	public Vector add(Vector v) {
		return add(v.x, v.y);
	}

	public Vector sub(float x, float y) {
		return new Vector(this.x - x, this.y - y);
	}

	public Vector sub(Vector v) {
		return sub(v.x, v.y);
	}

	public Vector mul(float x, float y) {
		return new Vector(this.x * x, this.y * y);
	}

	public Vector mul(Vector v) {
		return mul(v.x, v.y);
	}

	public Vector div(float x, float y) {
		return new Vector(this.x / x, this.y / y);
	}

	public Vector div(Vector v) {
		return div(v.x, v.y);
	}

	public float dot(Vector v) {
		return x * v.x + y * v.y;
	}

	public float lengthSq() {
		return x * x + y * y;
	}

	public float length() {
		return (float) Math.sqrt(lengthSq());
	}

	public Vector normalised() {
		float len = length();
		return div(len, len);
	}

	public VectorInt toVectorInt() {
		return new VectorInt((int) Math.floor(x), (int) Math.floor(y));
	}

	public Point toPoint() {
		return Point.getPoint((int) Math.floor(x), (int) Math.floor(y));
	}
}
