package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.Tile;
import pw.lemmmy.jrogue.dungeon.TileType;

import java.util.ArrayList;
import java.util.List;

public class AStarPathFinder {
	public static Path findPath(Level level, int sx, int sy, int tx, int ty, int maxSearchDistance, boolean allowDiagonalMovement) {
		if (level.getTile(tx, ty).getSolidity() == TileType.Solidity.SOLID) {
			return null; // don't do anything if we can't even go there in the first place
		}

		List<Node> closed = new ArrayList<>();
		List<Node> open = new ArrayList<>();

		int width = level.getWidth();
		int height = level.getHeight();

		Node[] nodes = new Node[width * height];

		for (int i = 0; i < width * height; i++) {
			nodes[i] = new Node(i % width, (int) Math.floor(i / width));
		}

		nodes[width * sy + sx].cost = 0;
		nodes[width * sy + sx].depth = 0;

		open.add(nodes[width * sy + sx]);

		nodes[width * ty + tx].parent = null;

		int maxDepth = 0;

		while ((maxDepth < maxSearchDistance) && (open.size() != 0)) {
			Node current = open.get(0);

			if (current == nodes[width * ty + tx]) {
				break;
			}

			open.remove(current);
			closed.add(current);

			for (int x = -1; x < 2; x++) {
				for (int y = -1; y < 2; y++) {
					if (x == 0 && y == 0) {
						continue;
					}

					if (!allowDiagonalMovement) {
						if (x != 0 && y != 0) {
							continue;
						}
					}

					int xp = x + current.x;
					int yp = y + current.y;

					if (isValidLocation(level, xp, yp)) {
						float nextStepCost = current.cost + 1;
						Node neighbour = nodes[width * yp + xp];

						if (nextStepCost < neighbour.cost) {
							if (open.contains(neighbour)) {
								open.remove(neighbour);
							}

							if (closed.contains(neighbour)) {
								closed.remove(neighbour);
							}
						}

						if (!open.contains(neighbour) && !closed.contains(neighbour)) {
							neighbour.cost = nextStepCost;
							neighbour.heuristic = getHeuristicCost(xp, yp, tx, ty);
							maxDepth = Math.max(maxDepth, neighbour.setParent(current));
							open.add(neighbour);
						}
					}
				}
			}
		}

		if (nodes[width * ty + tx].parent == null) {
			return null;
		}

		Path path = new Path();
		Node target = nodes[width * ty + tx];

		while (target != nodes[width * sy + sx]) {
			path.prependStep(level.getTileInfo(target.x, target.y));
			target = target.parent;
		}

		path.prependStep(level.getTileInfo(sx, sy));

		return path;
	}

	private static boolean isValidLocation(Level level, int x, int y) {
		return !(x < 0 || x > level.getWidth() ||
				y < 0 || y > level.getHeight()) &&
				level.getTile(x, y).getSolidity() != TileType.Solidity.SOLID;
	}

	private static float getHeuristicCost(int x, int y, int tx, int ty) {
		float dx = tx - x;
		float dy = ty - y;

		return (float) (Math.sqrt((dx * dx) + (dy * dy)));
	}

	private static class Node implements Comparable {
		private int x;
		private int y;
		private int depth;
		private float cost;
		private float heuristic;
		private Node parent;

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int setParent(Node parent) {
			depth = parent.depth + 1;
			this.parent = parent;

			return depth;
		}

		public int compareTo(Object other) {
			Node o = (Node) other;

			float f = heuristic + cost;
			float of = o.heuristic + o.cost;

			return (int) Math.max(-1, Math.min(1, of - f));
		}
	}

	public static class Path {
		private List<Tile> steps = new ArrayList<>();

		public int getLength() {
			return steps.size();
		}

		public Tile getStep(int index) {
			return steps.get(index);
		}

		public int getX(int index) {
			return steps.get(index).getX();
		}

		public int getY(int index) {
			return steps.get(index).getY();
		}

		public void addStep(Tile step) {
			steps.add(step);
		}

		public void prependStep(Tile step) {
			steps.add(0, step);
		}

		public boolean contains(Tile step) {
			return steps.contains(step);
		}
	}
}
