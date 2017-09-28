package jr.rendering.gdx2d.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.rendering.gdx2d.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ItemRendererGold extends ItemRenderer {
	private int[] values = new int[]{1, 2, 5, 10, 20, 30, 50, 100, 200, 300};
	
	private List<TextureRegion> images = new ArrayList<>();
	private List<TextureRegion> imagesDrawable = new ArrayList<>();
	
	public ItemRendererGold() {
		for (int i = 0; i < values.length; i++) {
			images.add(getImageFromSheet("textures/items.png", i, 7));
			imagesDrawable
				.add(ImageLoader.getImageFromSheet("textures/items.png", i, 7, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false));
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, ItemStack itemStack, Item item, boolean reflect) {
		return getImageFromAmount(itemStack.getCount(), true);
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y, boolean reflect) {
		drawItem(batch, getTextureRegion(dungeon, itemStack, item, reflect), x, y, reflect);
	}
	
	private TextureRegion getImageFromAmount(int count, boolean flipped) {
		int value = 1;
		
		for (int i = 0; i < values.length; i++) {
			if (count >= values[i]) {
				value = i;
			} else {
				break;
			}
		}
		
		if (flipped) {
			return images.get(value);
		} else {
			return imagesDrawable.get(value);
		}
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		return new TextureRegionDrawable(getImageFromAmount(itemStack.getCount(), false));
	}
}
