package pw.lemmmy.jrogue.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

public abstract class ItemRenderer {
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y);

	protected TextureRegion getImageFromSheet(String sheetName, int sheetX, int sheetY) {
		return ImageLoader.getImageFromSheet(sheetName, sheetX, sheetY);
	}

	protected void drawItem(SpriteBatch batch, TextureRegion image, int x, int y) {
		if (image != null) {
			batch.draw(image, x, y);
		}
	}

	public abstract Drawable getDrawable(ItemStack itemStack, Item item);
}
