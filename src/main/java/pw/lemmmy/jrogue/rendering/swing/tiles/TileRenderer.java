package pw.lemmmy.jrogue.rendering.swing.tiles;

import pw.lemmmy.jrogue.dungeon.Dungeon;

import java.awt.*;

public abstract class TileRenderer {
	public abstract void draw(Graphics2D g2d, Dungeon d, int x, int y);
}
