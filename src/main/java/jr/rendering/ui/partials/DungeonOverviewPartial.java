package jr.rendering.ui.partials;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.states.TileStateClimbable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DungeonOverviewPartial extends WidgetGroup {
	private static final int NODE_WIDTH = 125;
	private static final int NODE_PADDING = 20;
	
	private Skin skin;
	
	private Dungeon dungeon;
	
	int width;
	int height;
	
	private Map<Integer, Integer> rowHeights = new HashMap<>();
	
	public DungeonOverviewPartial(Skin skin, Dungeon dungeon) {
		this.skin = skin;
		this.dungeon = dungeon;
		
		drawTree(analyseDungeon());
	}
	
	private Node analyseDungeon() {
		UUID firstLevelUUID = UUID.fromString(dungeon.getPersistence().getString("firstLevel"));
		Level firstLevel = dungeon.getLevelFromUUID(firstLevelUUID);
		
		Node rootNode = new Node(firstLevel, null);
		
		initialiseNodes(rootNode);
		sortChildren(rootNode);
		initialiseNodeDepth(rootNode, 0);
		calculateInitialNodeX(rootNode);
		checkForConflicts(rootNode);
		calculateFinalPositions(rootNode, 0);
		
		return rootNode;
	}
	
	private void initialiseNodes(Node node) {
		Arrays.stream(node.level.getTileStore().getTiles())
			.filter(t -> (t.getType().getFlags() & TileFlag.DOWN) == TileFlag.DOWN)
			.filter(t -> t.hasState() && t.getState() instanceof TileStateClimbable)
			.forEach(t -> {
				TileStateClimbable tsc = (TileStateClimbable) t.getState();
				
				tsc.getLinkedLevel().ifPresent(level -> {
					Node newNode = new Node(level, node);
					node.addChild(newNode);
					
					initialiseNodes(newNode);
				});
			});
	}
	
	private void sortChildren(Node node) {
		node.children.sort(Comparator.comparingLong(n -> n.level.getTurnCreated()));
	}
	
	private void initialiseNodeDepth(Node node, int depth) {
		node.depth = depth;
		node.children.forEach(n -> initialiseNodeDepth(n, depth + 1));
	}
	
	private void calculateInitialNodeX(Node node) {
		node.children.forEach(this::calculateInitialNodeX);
		
		if (node.children.size() == 0) {
			node.x = 0;
			node.getPreviousSibling().ifPresent(previousNode -> node.x = previousNode.x + 1);
		} else if (node.children.size() == 1) {
			if (node.getPreviousSibling().isPresent()) {
				node.x = node.getPreviousSibling().get().x + 1;
				node.mod = node.x - node.children.get(0).x;
			} else {
				node.x = node.children.get(0).x;
			}
		} else {
			float leftX = node.children.get(0).x;
			float rightX = node.children.get(node.children.size() - 1).x;
			float mid = (leftX + rightX) / 2;
			
			if (node.getPreviousSibling().isPresent()) {
				node.x = node.getPreviousSibling().get().x + 1;
				node.mod = node.x - mid;
			} else {
				node.x = mid;
			}
		}
		
		if (node.children.size() > 0 && node.getPreviousSibling().isPresent()) {
			checkForConflicts(node);
		}
	}
	
	private void calculateFinalPositions(Node node, float modSum) {
		node.x += modSum;
		node.children.forEach(n -> calculateFinalPositions(n, modSum + node.mod));
	}
	
	private void checkForConflicts(Node node) {
		if (node.parent == null) return;
		
		float minDistance = 1;
		float shiftValue = 0;
		
		NavigableMap<Integer, Float> nodeContour = new TreeMap<>();
		getLeftContour(node, 0, nodeContour);
		
		Node sibling = node.parent.children.get(0);
		while(sibling != null && sibling != node) {
			NavigableMap<Integer, Float> siblingContour = new TreeMap<>();
			getRightContour(sibling, 0, siblingContour);
			
			for (int level = node.depth + 1; level <= Math.min(siblingContour.lastKey(), nodeContour.lastKey()); level++) {
				float distance = nodeContour.get(level) - siblingContour.get(level);
				
				if (distance + shiftValue < minDistance) {
					shiftValue = minDistance - distance;
				}
			}
			
			if (shiftValue > 0) {
				node.x += shiftValue;
				node.mod += shiftValue;
				
				centerNodesBetween(node, sibling);
				
				shiftValue = 0;
			}
			
			sibling = sibling.getNextSibling().orElse(null);
		}
	}
	
	private void centerNodesBetween(Node leftNode, Node rightNode) {
		if (leftNode.parent == null) return;
		
		int leftIndex = leftNode.parent.children.indexOf(leftNode);
		int rightIndex = leftNode.parent.children.indexOf(rightNode);
		
		int nodesBetween = (rightIndex - leftIndex) - 1;
		
		if (nodesBetween > 0) {
			float distanceBetweenNodes = (leftNode.x - rightNode.x) / (nodesBetween + 1);
			
			int count = 1;
			
			for (int i = leftIndex + 1; i < rightIndex; i++) {
				Node middleNode = leftNode.parent.children.get(i);
				
				float desiredX = rightNode.x + (distanceBetweenNodes * count);
				float offset = desiredX - middleNode.x;
				middleNode.x += offset;
				middleNode.mod += offset;
				
				count++;
			}
			
			checkForConflicts(leftNode);
		}
	}
	
	private void checkAllChildren(Node node) {
		Map<Integer, Float> nodeContour = new HashMap<>();
		getLeftContour(node, 0, nodeContour);
		
		AtomicReference<Float> shiftAmount = new AtomicReference<>(0f);
		
		nodeContour.forEach((k, v) -> {
			if (v + shiftAmount.get() < 0) {
				shiftAmount.set(v * -1);
			}
		});
		
		if (shiftAmount.get() > 0) {
			node.x += shiftAmount.get();
			node.mod += shiftAmount.get();
		}
	}
	
	private void getLeftContour(Node node, float modSum, Map<Integer, Float> nodeContour) {
		if (!nodeContour.containsKey(node.depth)) {
			nodeContour.put(node.depth, node.x + modSum);
		} else {
			nodeContour.put(node.depth, Math.min(nodeContour.get(node.depth), node.x + modSum));
		}
		
		node.children.forEach(n -> getLeftContour(n, modSum + node.mod, nodeContour));
	}
	
	private void getRightContour(Node node, float modSum, Map<Integer, Float> nodeContour) {
		if (!nodeContour.containsKey(node.depth)) {
			nodeContour.put(node.depth, node.x + modSum);
		} else {
			nodeContour.put(node.depth, Math.max(nodeContour.get(node.depth), node.x + modSum));
		}
		
		node.children.forEach(n -> getRightContour(n, modSum + node.mod, nodeContour));
	}
	
	private void drawTree(Node rootNode) {
		addTreePart(rootNode);
		
		height += rowHeights.values().stream().mapToInt(Integer::intValue).sum();
		height += rowHeights.size() * (NODE_PADDING * 2);
		
		positionTreePart(rootNode);
		
		System.out.println(rootNode);
	}
	
	private void addTreePart(Node node) {
		Table nodeTable = new Table(skin);
		node.table = nodeTable;
		
		nodeTable.add(new Label(node.level.toString(), skin));
		
		nodeTable.setWidth(NODE_WIDTH);
		nodeTable.layout();
		nodeTable.setX(node.x * NODE_WIDTH);
		
		if (width < (node.x * (NODE_WIDTH + NODE_PADDING)) + (NODE_PADDING * 2)) {
			width = (int) ((node.x * (NODE_WIDTH + NODE_PADDING)) + (NODE_PADDING * 2));
		}
		
		if (nodeTable.getPrefHeight() > rowHeights.getOrDefault(node.depth, 0)) {
			rowHeights.put(node.depth, (int) nodeTable.getPrefHeight());
		}
		
		addActor(nodeTable);
		
		node.children.forEach(this::addTreePart);
	}
	
	private void positionTreePart(Node node) {
		Table nodeTable = node.table;
		
		int rowHeight = rowHeights.get(node.depth);
		int tableHeight = (int) nodeTable.getPrefHeight();
		int localY = rowHeight - tableHeight / 2;
		
		nodeTable.setY(height - ((node.depth * (rowHeight + NODE_PADDING)) + localY));
		
		node.children.forEach(this::positionTreePart);
	}
	
	@Override
	public float getMinWidth() {
		return width;
	}
	
	@Override
	public float getMinHeight() {
		return height;
	}
	
	@Override
	public float getMaxWidth() {
		return width;
	}
	
	@Override
	public float getMaxHeight() {
		return height;
	}
	
	@Override
	public float getPrefWidth() {
		return width;
	}
	
	@Override
	public float getPrefHeight() {
		return height;
	}
	
	private class Node {
		private Level level;
		@Nullable private Node parent;
		private List<Node> children = new ArrayList<>();
		private float x, mod;
		private int depth;
		private Table table;
		
		public Node(Level level, Node parent) {
			this.level = level;
			this.parent = parent;
		}
		
		public void addChild(Node node) {
			children.add(node);
		}
		
		public Optional<Node> getPreviousSibling() {
			if (parent == null) return Optional.empty();
			
			int i = parent.children.indexOf(this);
			
			if (i > 0) {
				return Optional.of(parent.children.get(i - 1));
			} else {
				return Optional.empty();
			}
		}
		
		public Optional<Node> getNextSibling() {
			if (parent == null) return Optional.empty();
			
			int i = parent.children.indexOf(this);
			
			if (i + 1 < parent.children.size()) {
				return Optional.of(parent.children.get(i + 1));
			} else {
				return Optional.empty();
			}
		}
	}
}
