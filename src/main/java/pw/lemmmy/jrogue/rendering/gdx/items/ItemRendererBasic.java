package pw.lemmmy.jrogue.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.items.Item;

public class ItemRendererBasic extends ItemRenderer {
	private TextureRegion image;

	public ItemRendererBasic(String sheetName, int sheetX, int sheetY) {
		image = getImageFromSheet(sheetName, sheetX, sheetY);
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, Item item, int x, int y) {
		drawItem(batch, image, x, y);
	}
}
