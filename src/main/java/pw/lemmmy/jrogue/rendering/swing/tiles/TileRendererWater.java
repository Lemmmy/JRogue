package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Tiles;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileRendererWater extends TileRendererBlob {
	private BufferedImage water;
	private BufferedImage floor;

	private BufferedImage[] overlayImages = new BufferedImage[BLOB_SHEET_WIDTH * BLOB_SHEET_HEIGHT];

	private boolean connectToOthers;
	private Tiles self;

	public TileRendererWater(int sheetX, int sheetY, int floorSheetX, int floorSheetY) {
		this(sheetX, sheetY, floorSheetX, floorSheetY, true, null);
	}

	public TileRendererWater(int sheetX, int sheetY, int floorSheetX, int floorSheetY, boolean connectToOthers, Tiles self) {
		super(1, 0);

		this.connectToOthers = connectToOthers;
		this.self = self;

		BufferedImage sheet = ImageLoader.getImage("tiles.png");

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", "tiles.png");
			System.exit(1);
		}

		water = sheet.getSubimage(sheetX * TileMap.TILE_WIDTH, sheetY * TileMap.TILE_HEIGHT, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
		floor = sheet.getSubimage(floorSheetX * TileMap.TILE_WIDTH, floorSheetY * TileMap.TILE_HEIGHT, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);

		loadBlob(overlayImages, 2, 0);
	}

	@Override
	boolean isJoinedTile(Tiles tile) {
		if (connectToOthers) {
			return tile == null /* so the water looks like its going offscreen */ ||
				tile == Tiles.TILE_ROOM_WATER ||
				tile == Tiles.TILE_GROUND_WATER;
		} else {
			return tile == self;
		}
	}

	@Override
	public void draw(Graphics2D g2d, Dungeon d, int x, int y) {
		BufferedImage blobImage = getImageFromMask(getPositionMask(d.getLevel(), x, y));
		BufferedImage t = new BufferedImage(TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		drawTile(g2d, water, x, y);

		Graphics2D gbi = t.createGraphics();

		drawTile(gbi, floor, 0, 0);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_IN);
		gbi.setComposite(ac);
		drawTile(gbi, blobImage, 0, 0);

		drawTile(g2d, t, x, y);

		Composite oc = g2d.getComposite();

		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
		g2d.setComposite(ac);

		drawTile(g2d, getImageFromMask(overlayImages, getPositionMask(d.getLevel(), x, y)), x, y);

		g2d.setComposite(oc);
	}
}
