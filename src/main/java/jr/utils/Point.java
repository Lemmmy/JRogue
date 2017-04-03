package jr.utils;

import com.badlogic.gdx.utils.Pool;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Point implements Pool.Poolable {
	private static final Pool<Point> pointPool = new Pool<Point>() {
		@Override
		protected Point newObject() {
			return new Point();
		}
	};

	private int x;
	private int y;

	public static Point getPoint(int x, int y) {
		Point p = pointPool.obtain();
		p.init(x, y);
		return p;
	}

	private Point() {
		reset();
	}

	public Point(int x, int y) {
		init(x, y);
	}

	@Override
	public void reset() {
		this.x = 0;
		this.y = 0;
	}

	private void init(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		
		Point point = (Point) o;
		
		return x == point.x && y == point.y;
	}
	
	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}
	
	public Point clampedDelta(Point point) {
		return new Point(
			Math.max(-1, Math.min(this.getX() - point.getX(), 1)),
			Math.max(-1, Math.min(this.getY() - point.getY(), 1))
		);
	}
	
	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}
	
	@Override
	public String toString() {
		return x + ", " + y;
	}
}
