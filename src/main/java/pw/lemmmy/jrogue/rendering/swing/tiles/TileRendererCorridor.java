package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.TileType;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileRendererCorridor extends TileRendererBlob {
	private BufferedImage corridor;
	private BufferedImage empty;

	public TileRendererCorridor() {
		super(1, 0);

		BufferedImage sheet = ImageLoader.getImage("tiles.png");

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", "tiles.png");
			System.exit(1);
		}

		corridor = sheet.getSubimage(0, TileMap.TILE_HEIGHT, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
		empty = sheet.getSubimage(TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}

	@Override
	boolean isJoinedTile(TileType tile) {
		return tile == TileType.TILE_CORRIDOR || tile == TileType.TILE_ROOM_DOOR || tile == TileType.TILE_ROOM_WALL;
	}

	@Override
	public void draw(Graphics2D g2d, Dungeon d, int x, int y) {
		BufferedImage blobImage = getImageFromMask(getPositionMask(d.getLevel(), x, y));
		BufferedImage t = new BufferedImage(TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		drawTile(g2d, corridor, x, y);

		Graphics2D gbi = t.createGraphics();

		drawTile(gbi, empty, 0, 0);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_IN);
		gbi.setComposite(ac);
		drawTile(gbi, blobImage, 0, 0);

		drawTile(g2d, t, x, y);
		drawLight(g2d, d, x, y);
	}
}
