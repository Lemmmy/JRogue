package jr.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.HasMaterial;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Material;
import jr.rendering.gdx.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ItemRendererSword extends ItemRenderer {
	private List<TextureRegion> images = new ArrayList<>();
	private List<TextureRegion> imagesDrawable = new ArrayList<>();
	
	public ItemRendererSword(int sheetX, int sheetY) {
		for (int i = 0; i < 9; i++) {
			images.add(getImageFromSheet("textures/items.png", sheetX + i, sheetY));
			imagesDrawable.add(ImageLoader.getImageFromSheet(
					"textures/items.png",
				sheetX + i,
				sheetY,
				ItemMap.ITEM_WIDTH,
				ItemMap.ITEM_HEIGHT,
				false
			));
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y) {
		drawItem(batch, getImageFromMaterial(((HasMaterial) item).getMaterial(), true), x, y);
	}
	
	private TextureRegion getImageFromMaterial(Material material, boolean flipped) {
		int i = material.ordinal();
		
		if (flipped) {
			return images.get(i);
		} else {
			return imagesDrawable.get(i);
		}
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		return new TextureRegionDrawable(getImageFromMaterial(((HasMaterial) item).getMaterial(), false));
	}
}
