package jr.dungeon.entities.monsters.ai;

import jr.dungeon.Level;
import jr.dungeon.tiles.TileType;
import jr.utils.Path;
import jr.utils.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Attempts to find and return a {@link Path} according to the
 * <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* pathfinding algorithm</a>.
 */
public class AStarPathfinder {
	private static final float d = 1;
	private static final float d2 = (float) Math.sqrt(2);
	
	/**
	 * Attempts to find and return a {@link Path} according to the
	 * <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* pathfinding algorithm</a>.
	 *
	 * @param level The Level to search for a path on.
	 * @param sx The start/source X position.
	 * @param sy The start/source Y position.
	 * @param tx The destination/target X position.
	 * @param ty The destination/target Y position.
	 * @param maxSearchDistance The maximum distance allowed to search for a path within.
	 * @param allowDiagonalMovement Whether or not moving diagonally should be allowed (i.e. moving on both axis
	 *                                 simultaneously)
	 * @param avoidTiles A List of {@link TileType TileTypes} to avoid. The returned Path will never contain one of
	 *                      these tiles unless it is the source tile.
	 *
	 * @return The {@link Path} that was found, or null if one wasn't found within the maximum search distance, or it
	 * was not possible to reach the target position.
	 */
	public Path findPath(Level level,
						 int sx,
						 int sy,
						 int tx,
						 int ty,
						 int maxSearchDistance,
						 boolean allowDiagonalMovement,
						 List<TileType> avoidTiles) {
		// For full description of algorithm, see https://en.wikipedia.org/wiki/A*_search_algorithm
		
		if (level.tileStore.getTileType(tx, ty).getSolidity() == TileType.Solidity.SOLID) {
			return null; // don't do anything if we can't even go there in the first place
		}
		
		// The set of nodes that have already been evaluated.
		List<Node> closed = new ArrayList<>();
		// The set of discovered nodes that are not evaluated yet.
		List<Node> open = new ArrayList<>();
		
		int width = level.getWidth();
		int height = level.getHeight();
		
		// Initialise every possible node in the level.
		Node[] nodes = new Node[width * height];
		
		for (int i = 0; i < width * height; i++) {
			nodes[i] = new Node(i % width, (int) Math.floor(i / width));
		}
		
		// We're already at the source node, so set the cost and depth to zero.
		nodes[width * sy + sx].cost = 0;
		nodes[width * sy + sx].depth = 0;
		
		// Starts the list of open nodes off with the source node.
		open.add(nodes[width * sy + sx]);
		
		// The target node has no parent yet.
		nodes[width * ty + tx].parent = null;
		
		// While we haven't exceeded our maximum search depth,
		int maxDepth = 0;
		
		while (maxDepth < maxSearchDistance && open.size() != 0) {
			// pull out the first node in our open list - this is determined to be the most likely to be the next
			// step based on the heuristic.
			
			Node current = open.get(0);
			
			if (current == nodes[width * ty + tx]) {
				// We've reached the target.
				break;
			}
			
			// Take the current node out of the open nodes list, and put it in the closed nodes list.
			open.remove(current);
			closed.add(current);
			
			// Search through the adjacent tiles.
			for (int x = -1; x < 2; x++) {
				for (int y = -1; y < 2; y++) {
					// The current tile is not a neighbour.
					if (x == 0 && y == 0) {
						continue;
					}
					
					// If diagonal movement is not allowed, don't check a tile where both axis deltas are nonzero.
					// In other words, only one of x or y can be set.
					if (!allowDiagonalMovement) {
						if (x != 0 && y != 0) {
							continue;
						}
					}
					
					int xp = x + current.x;
					int yp = y + current.y;
					
					// Check if the tile is in the map, non-solid, and not in the list of tiles to avoid.
					if (isValidLocation(level, xp, yp, avoidTiles)) {
						// The cost to get to this node is the current node's cost plus the movement cost to reach
						// this node. Note that the heuristic value is only used in the sorted open list.
						float nextStepCost = current.cost + getHeuristicCost(current.x, current.y, xp, yp);
						Node neighbour = nodes[width * yp + xp];
						
						// Checks we haven't found a better route to a node we'd previously considered searched (i.e.
						// it's in the open or closed lists). If we've found a better route to the node (the cost is
						// less than the recorded cost), then remove it from the lists it's in to mark it as
						// un-searched.
						if (nextStepCost < neighbour.cost) {
							if (open.contains(neighbour)) {
								open.remove(neighbour);
							}
							
							if (closed.contains(neighbour)) {
								closed.remove(neighbour);
							}
						}
						
						// If the node hasn't already been processed and discarded, then reset its cost to our
						// current cost, and add it as a next possible step (i.e. to the open list).
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
			// No path was found.
			return null;
		}
		
		// Create and return a {@link Path} containing our nodes.
		Path path = new Path();
		Node target = nodes[width * ty + tx];
		
		// Since we know the path we can traverse the parents of the target node until we reach the source to build
		// the path. Since we work backwards, we prepend the steps.
		while (target != nodes[width * sy + sx]) {
			path.prependStep(level.tileStore.getTile(target.x, target.y));
			target = target.parent;
		}
		
		// Finally add the source to the path too.
		path.prependStep(level.tileStore.getTile(sx, sy));
		
		path.lock();
		
		return path;
	}
	
	/**
	 * Checks if the specified position is a valid location to move in the {@link Level}. Also checks if the position
	 * is in the list of {@link TileType TileTypes} to avoid.
	 *
	 * @param level The level to check against.
	 * @param x The X position to check.
	 * @param y The Y position to check.
	 * @param avoidTiles The list of {@link TileType TileTypes} to avoid. Can be empty.
	 *
	 * @return Whether or not this tile can be moved to.
	 */
	public boolean isValidLocation(Level level, int x, int y, List<TileType> avoidTiles) {
		return !(x < 0 || x >= level.getWidth() ||
			y < 0 || y >= level.getHeight()) &&
			level.tileStore.getTile(x, y) != null &&
			level.tileStore.getTileType(x, y).getSolidity() != TileType.Solidity.SOLID &&
			!avoidTiles.contains(level.tileStore.getTileType(x, y));
	}
	
	/**
	 * Calculates the <a href="https://en.wikipedia.org/wiki/Heuristic_(computer_science)">heuristic</a> cost of
	 * moving from tile <code>A</code> to tile <code>B</code>. Uses the octile distance heuristic, using 1 as a cost
	 * for cardinal movements, and <code>sqrt(2)</code> (<code>~1.41</code>) for diagonal movements.
	 *
	 * @param ax The source X position.
	 * @param ay The source Y position.
	 * @param bx The target X position.
	 * @param by The target Y position.
	 *
	 * @return The heuristic cost for moving from A to B.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Heuristic_(computer_science)">Wikipedia article on Heuristics</a>
	 * @see <a href="http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html">Page 2 of Amit's Thoughts on
	 * Pathfinding, describing A*'s use of Heuristics in game programming.</a>
	 * @see <a href="http://movingai.com/astar.html">Nathan Sturtevant's article on A* tie-breaking.</a>
	 *
	 * @see Utils#octileDistance(int, int, int, int, float, float)
	 */
	public float getHeuristicCost(int ax, int ay, int bx, int by) {
		return Utils.octileDistance(ax, ay, bx, by, d, d2);
	}
	
	/**
	 * Simple object for the {@link AStarPathfinder}'s nodes - embodies a point in a {@link Level}, the search depth
	 * of the node, path and heuristic cost, and parent node.
	 */
	@Getter
	public class Node implements Comparable<Node> {
		/** The X coordinate of the node. */
		private int x;
		/** The Y coordinate of the node. */
		private int y;
		/** The search depth of the node. */
		private int depth;
		/** The path cost of the node. */
		private float cost;
		/**
		 * The heuristic cost of the node.
		 *
		 * @see AStarPathfinder#getHeuristicCost(int, int, int, int)
		 */
		private float heuristic;
		/** The parent of the node - how we reached it in the search. */
		private Node parent;
		
		/**
		 * Simple object for the {@link AStarPathfinder}'s nodes - embodies a point in a {@link Level}, the search depth
		 * of the node, path and heuristic cost, and parent node.
		 *
		 * @param x The X coordinate of the node.
		 * @param y The Y coordinate of the node.
		 */
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Sets the parent of this node.
		 *
		 * @param parent The parent node which led us to this node.
		 *
		 * @return The depth we have now reached in searching.
		 */
		public int setParent(Node parent) {
			depth = parent.depth + 1;
			this.parent = parent;
			
			return depth;
		}
		
		/**
		 * @see Comparable#compareTo(Object)
		 */
		public int compareTo(Node other) {
			float f = heuristic + cost;
			float of = other.heuristic + other.cost;
			
			return (int) Math.max(-1, Math.min(1, of - f));
		}
	}
}
