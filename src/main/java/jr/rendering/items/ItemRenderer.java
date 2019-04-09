package jr.rendering.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.rendering.assets.UsesAssets;

public abstract class ItemRenderer implements UsesAssets {
	public abstract TextureRegion getTextureRegion(Dungeon dungeon, ItemStack itemStack, Item item, boolean reflect);
	
	public abstract void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y, boolean reflect);
	
	protected void drawItem(SpriteBatch batch, TextureRegion image, int x, int y, boolean reflect) {
		if (image != null) {
			if (reflect) {
				batch.draw(
					image,
					x, y,
					0.0f, 0.0f,
					image.getRegionWidth(), image.getRegionHeight(),
					1.0f, -1.0f,
					0.0f
				);
			} else {
				batch.draw(image, x, y);
			}
		}
	}
	
	public abstract Drawable getDrawable(ItemStack itemStack, Item item);
}
