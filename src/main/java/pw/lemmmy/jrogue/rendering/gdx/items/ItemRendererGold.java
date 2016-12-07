package pw.lemmmy.jrogue.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ItemRendererGold extends ItemRenderer {
	private int[] values = new int[]{1, 2, 5, 10, 20, 30, 50, 100, 200, 300};

	private List<TextureRegion> images = new ArrayList<>();
	private List<TextureRegion> imagesDrawable = new ArrayList<>();

	public ItemRendererGold() {
		for (int i = 0; i < values.length; i++) {
			images.add(getImageFromSheet("items.png", i, 7));
			imagesDrawable
				.add(ImageLoader.getImageFromSheet("items.png", i, 7, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false));
		}
	}

	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y) {
		drawItem(batch, getImageFromAmount(itemStack.getCount(), true), x, y);
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
