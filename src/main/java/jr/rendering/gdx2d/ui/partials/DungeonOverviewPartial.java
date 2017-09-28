package jr.rendering.gdx2d.ui.partials;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.generators.Climate;
import jr.dungeon.generators.rooms.features.SpecialRoomFeature;
import jr.dungeon.tiles.TileFlag;
import jr.dungeon.tiles.states.TileStateClimbable;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.*;

public class DungeonOverviewPartial extends WidgetGroup {
	private static final int NODE_WIDTH = 200;
	private static final int NODE_SPACING_H = 50;
	private static final int NODE_SPACING_V = 20;
	
	private static final Map<Climate, String> climateDrawableMap = new HashMap<>();
	
	static {
		climateDrawableMap.put(Climate.WARM, "warm");
		climateDrawableMap.put(Climate.MID, "mid");
		climateDrawableMap.put(Climate.COLD, "cold");
	}
	
	private Skin skin;
	
	private Dungeon dungeon;
	
	int width;
	int height;
	
	private Map<Integer, Integer> rowHeights = new HashMap<>();
	private int yOffset;
	
	private Node rootNode;
	
	private Texture whiteTexture;
	
	public DungeonOverviewPartial(Skin skin, Dungeon dungeon) {
		this.skin = skin;
		this.dungeon = dungeon;
		
		whiteTexture = skin.get("white", Texture.class);
		
		drawTree(analyseDungeon());
	}
	
	private Node analyseDungeon() {
		UUID firstLevelUUID = UUID.fromString(dungeon.serialiser.getPersistence().getString("firstLevel"));
		Level firstLevel = dungeon.getLevelFromUUID(firstLevelUUID);
		
		rootNode = new Node(firstLevel, null);
		
		initialiseNodes(rootNode);
		sortChildren(rootNode);
		initialiseNodeDepth(rootNode, 0);
		calculateInitialNodeX(rootNode);
		checkForConflicts(rootNode);
		calculateFinalPositions(rootNode, 0);
		
		return rootNode;
	}
	
	private void initialiseNodes(Node node) {
		Arrays.stream(node.level.tileStore.getTiles())
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
				node.x = node.getPreviousSibling().get().x + NODE_WIDTH + NODE_SPACING_H;
				node.mod = node.x - node.children.get(0).x;
			} else {
				node.x = node.children.get(0).x;
			}
		} else {
			float leftX = node.children.get(0).x;
			float rightX = node.children.get(node.children.size() - 1).x;
			float mid = (leftX + rightX) / 2;
			
			if (node.getPreviousSibling().isPresent()) {
				node.x = node.getPreviousSibling().get().x + NODE_WIDTH + NODE_SPACING_H;
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
		
		int nodesBetween = rightIndex - leftIndex - 1;
		
		if (nodesBetween > 0) {
			float distanceBetweenNodes = (leftNode.x - rightNode.x) / (nodesBetween + 1);
			
			int count = 1;
			
			for (int i = leftIndex + 1; i < rightIndex; i++) {
				Node middleNode = leftNode.parent.children.get(i);
				
				float desiredX = rightNode.x + distanceBetweenNodes * count;
				float offset = desiredX - middleNode.x;
				middleNode.x += offset;
				middleNode.mod += offset;
				
				count++;
			}
			
			checkForConflicts(leftNode);
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
		
		positionTreePart(rootNode);
	}
	
	private Drawable getNodeBackground(Level level) {
		if (climateDrawableMap.containsKey(level.getClimate())) {
			return skin.get(climateDrawableMap.get(level.getClimate()), NinePatchDrawable.class);
		} else {
			return skin.get(climateDrawableMap.get(Climate.WARM), NinePatchDrawable.class);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addTreePart(Node node) {
		Table nodeTable = new Table(skin);
		node.table = nodeTable;
		
		nodeTable.setBackground(getNodeBackground(node.level));
		nodeTable.add(new Label(node.level.toString(), skin, "large")).left().row();
		
		if (
			node.level.getPersistence().has("generatorPersistence") &&
			node.level.getPersistence().getJSONObject("generatorPersistence").has("roomFeatures")
		) {
			JSONObject generatorPersistence = node.level.getPersistence().getJSONObject("generatorPersistence");
			JSONObject roomFeatures = generatorPersistence.getJSONObject("roomFeatures");
			
			Table featuresTable = new Table(skin);
			
			roomFeatures.keySet().forEach(k -> {
				try {
					Class featureClass = Class.forName(k);
					SpecialRoomFeature feature = (SpecialRoomFeature) featureClass.newInstance();
					
					int count = roomFeatures.getInt(k);
					String name = feature.getName(count != 1);
					
					if (name == null) return;
					
					Label featureLabel = new Label(String.format(
						"%s %s",
						count == 1 ? "a" : String.format("[P_YELLOW]%,d[]", count),
						name
					), skin);
					
					featuresTable.add(featureLabel).left().row();
				} catch (Exception e) {
					ErrorHandler.error("Error in dungeon overview partial", e);
				}
			});
			
			nodeTable.add(featuresTable).left().row();
		}
		
		if (dungeon.getPlayer().getLevel() == node.level) {
			nodeTable.add(new Label("[WHITE]You died here", skin, "redBackground"))
				.padTop(4).padBottom(4).growX().row();
		}
		
		nodeTable.setX(node.x);
		nodeTable.pack();
		nodeTable.pad(8);
		nodeTable.left();
		nodeTable.setWidth(NODE_WIDTH);
		
		if (width < node.x) {
			width = (int) node.x;
		}
		
		int prefHeight = (int) (nodeTable.getPrefHeight() + NODE_SPACING_V);
		
		if (node.children.size() > 1) {
			prefHeight += NODE_SPACING_V + 1;
		}
		
		if (prefHeight > rowHeights.getOrDefault(node.depth, 0)) {
			rowHeights.put(node.depth, prefHeight);
		}
		
		addActor(nodeTable);
		
		node.children.forEach(this::addTreePart);
	}
	
	private void positionTreePart(Node node) {
		Table nodeTable = node.table;
		
		int rowY = 0;
		
		for (int i = node.depth; i > 0; i--) {
			rowY += rowHeights.get(i);
		}
		
		int rowHeight = rowHeights.get(node.depth);
		int tableHeight = (int) nodeTable.getPrefHeight();
		int localY = (rowHeight - tableHeight) / 2;
		
		if (node.children.size() > 1) {
			localY += NODE_SPACING_V + 1;
		}
		
		if (node.parent == null) {
			yOffset = height - (height - (rowY - localY));
			yOffset -= tableHeight;
		}
		
		nodeTable.setY(height - (rowY - localY) + yOffset);
		
		node.children.forEach(this::positionTreePart);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		validate();
		
		if (isTransform()) {
			applyTransform(batch, computeTransform());
			drawNodeLines(rootNode, batch);
			resetTransform(batch);
		} else {
			drawNodeLines(rootNode, batch);
		}
		
		super.draw(batch, parentAlpha);
	}
	
	private void drawNodeLines(Node node, Batch batch) {
		node.children.forEach(n -> drawNodeLines(n, batch));
		
		if (node.children.size() == 0) return;
		if (node.children.size() == 1) {
			Node child = node.children.get(0);
			
			int x = (int) (node.table.getX() + NODE_WIDTH / 2);
			int startY = (int) node.table.getY(); int endY = (int) child.table.getY();
			int width = 1; int height = endY - startY;
			
			batch.draw(whiteTexture, x, startY, width, height);
		} else {
			Node firstChild = node.children.get(0);
			Node lastChild = node.children.get(node.children.size() - 1);
			
			// top line stemming from node
			
			int x = (int) (node.table.getX() + NODE_WIDTH / 2);
			int startY = (int) node.table.getY(); int endY = (int) firstChild.table.getY();
			int width = 1; int height = (int) ((endY + firstChild.table.getPrefHeight() - startY) / 2);
			
			batch.draw(whiteTexture, x, startY, width, height);
			
			// horizontal line spanning all children
			
			int startX = (int) (firstChild.table.getX() + NODE_WIDTH / 2);
			int endX = (int) (lastChild.table.getX() + NODE_WIDTH / 2);
			int y = startY + height;
			width = endX - startX;
			
			batch.draw(whiteTexture, startX, y, width + 1, 1);
			
			// vertical lines going into children
			
			node.children.forEach(child -> {
				int cx = (int) (child.table.getX() + NODE_WIDTH / 2);
				batch.draw(whiteTexture, cx, y, 1, height - child.table.getPrefHeight() / 2);
			});
		}
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
