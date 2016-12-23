package pw.lemmmy.jrogue.dungeon.entities.monsters.ai;

import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Path;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

import java.util.ArrayList;
import java.util.List;

public class AStarPathfinder {
	public Path findPath(Level level,
								int sx,
								int sy,
								int tx,
								int ty,
								int maxSearchDistance,
								boolean allowDiagonalMovement,
								List<TileType> avoidTiles) {
		if (level.getTileType(tx, ty).getSolidity() == TileType.Solidity.SOLID) {
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

					if (isValidLocation(level, xp, yp, avoidTiles)) {
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
			path.prependStep(level.getTile(target.x, target.y));
			target = target.parent;
		}

		path.prependStep(level.getTile(sx, sy));

		return path;
	}

	public boolean isValidLocation(Level level, int x, int y, List<TileType> avoidTiles) {
		return !(x < 0 || x >= level.getWidth() ||
			y < 0 || y >= level.getHeight()) &&
			level.getTile(x, y) != null &&
			level.getTileType(x, y).getSolidity() != TileType.Solidity.SOLID &&
			!avoidTiles.contains(level.getTileType(x, y));
	}

	public float getHeuristicCost(int x, int y, int tx, int ty) {
		float dx = Math.abs(tx - x);
		float dy = Math.abs(ty - y);

		return (dx + dy) + Math.min(dx, dy);
	}

	public class Node implements Comparable<Node> {
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

		public int compareTo(Node other) {

			float f = heuristic + cost;
			float of = other.heuristic + other.cost;

			return (int) Math.max(-1, Math.min(1, of - f));
		}
	}
}
