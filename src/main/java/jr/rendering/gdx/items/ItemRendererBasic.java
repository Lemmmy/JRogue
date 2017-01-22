package jr.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.items.Item;
import jr.rendering.gdx.utils.ImageLoader;
import jr.dungeon.Dungeon;
import jr.dungeon.items.ItemStack;

public class ItemRendererBasic extends ItemRenderer {
	private TextureRegion image;
	private TextureRegion imageDrawable;
	
	public ItemRendererBasic(String sheetName, int sheetX, int sheetY) {
		image = getImageFromSheet(sheetName, sheetX, sheetY);
		imageDrawable = ImageLoader
			.getImageFromSheet(sheetName, sheetX, sheetY, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false);
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y) {
		drawItem(batch, image, x, y);
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		return new TextureRegionDrawable(imageDrawable);
	}
}
