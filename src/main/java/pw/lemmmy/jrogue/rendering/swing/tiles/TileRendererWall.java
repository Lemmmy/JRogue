package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Tiles;
import pw.lemmmy.jrogue.rendering.swing.utils.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileRendererWall extends TileRenderer {
	private BufferedImage wallH;
	private BufferedImage wallV;
	private BufferedImage wallCT;
	private BufferedImage wallCB;

	public TileRendererWall() {
		BufferedImage sheet = ImageLoader.getImage("tiles.png");

		if (sheet == null) {
			JRogue.getLogger().fatal("Failed to load spritesheet {}.", "tiles.png");
			System.exit(1);
		}

		wallH = sheet.getSubimage(TileMap.TILE_WIDTH, 0, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
		wallV = sheet.getSubimage(0, 0, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
		wallCT = sheet.getSubimage(2 * TileMap.TILE_WIDTH, 0, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
		wallCB = sheet.getSubimage(3 * TileMap.TILE_WIDTH, 0, TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
	}

	@Override
	public void draw(Graphics2D g2d, Dungeon d, int x, int y) {
		Tiles[] adjacentTiles = d.getLevel().getAdjacentTiles(x, y);

		boolean h = adjacentTiles[0] == Tiles.TILE_ROOM_WALL || adjacentTiles[1] == Tiles.TILE_ROOM_WALL;
		boolean v = adjacentTiles[2] == Tiles.TILE_ROOM_WALL || adjacentTiles[3] == Tiles.TILE_ROOM_WALL;

		if (h && !v) {
			drawTile(g2d, wallH, x, y);
		} else if (!h && v) {
			drawTile(g2d, wallV, x, y);
		} else {
			if (adjacentTiles[2] == Tiles.TILE_ROOM_WALL) {
				drawTile(g2d, wallCT, x, y);
			} else {
				drawTile(g2d, wallCB, x, y);
			}
		}
	}
}
