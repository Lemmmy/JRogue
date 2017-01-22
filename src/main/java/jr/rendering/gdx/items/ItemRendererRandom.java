package jr.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.items.Item;
import jr.rendering.gdx.utils.ImageLoader;
import jr.dungeon.Dungeon;
import jr.dungeon.items.ItemStack;

public class ItemRendererRandom extends ItemRenderer {
	protected TextureRegion[] images;
	protected TextureRegion[] imagesDrawable;
	private int count;
	
	public ItemRendererRandom(int sheetX, int sheetY, int count) {
		images = new TextureRegion[count];
		imagesDrawable = new TextureRegion[count];
		this.count = count;
		
		for (int i = 0; i < count; i++) {
			images[i] = getImageFromSheet("items.png", sheetX + i, sheetY);
			imagesDrawable[i] = ImageLoader
				.getImageFromSheet("items.png", sheetX + i, sheetY, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false);
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y) {
		drawItem(batch, images[item.getVisualID() % count], x, y);
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		return new TextureRegionDrawable(imagesDrawable[item.getVisualID() % count]);
	}
}
