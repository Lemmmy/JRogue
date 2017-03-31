package jr.utils;

import lombok.Data;

@Data
public class VectorInt {
	public static VectorInt ZERO = new VectorInt(0, 0);

	private final int x, y;

	public VectorInt(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public VectorInt set(int x, int y) {
		return new VectorInt(x, y);
	}

	public VectorInt add(int x, int y) {
		return new VectorInt(this.x + x, this.y + y);
	}

	public VectorInt add(VectorInt v) {
		return add(v.x, v.y);
	}

	public VectorInt sub(int x, int y) {
		return new VectorInt(this.x - x, this.y - y);
	}

	public VectorInt sub(VectorInt v) {
		return sub(v.x, v.y);
	}

	public VectorInt mul(int x, int y) {
		return new VectorInt(this.x * x, this.y * y);
	}

	public VectorInt mul(VectorInt v) {
		return mul(v.x, v.y);
	}

	public VectorInt div(int x, int y) {
		return new VectorInt(this.x / x, this.y / y);
	}

	public VectorInt div(VectorInt v) {
		return div(v.x, v.y);
	}

	public int dot(VectorInt v) {
		return x * v.x + y * v.y;
	}

	public int lengthSq() {
		return x * x + y * y;
	}

	public int length() {
		return (int) Math.sqrt(lengthSq());
	}

	public VectorInt normalised() {
		int len = length();
		return div(len, len);
	}

	public Vector toVectorFloat() {
		return new Vector(x, y);
	}

	public Point toPoint() {
		return Point.getPoint((int) Math.floor(x), (int) Math.floor(y));
	}
}
