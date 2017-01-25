package jr.utils;

import com.badlogic.gdx.utils.Pool;

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
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
