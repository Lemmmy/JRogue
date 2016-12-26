package pw.lemmmy.jrogue.rendering.gdx.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.items.HasMaterial;
import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemStack;
import pw.lemmmy.jrogue.dungeon.items.Material;
import pw.lemmmy.jrogue.rendering.gdx.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ItemRendererSword extends ItemRenderer {
	private List<TextureRegion> images = new ArrayList<>();
	private List<TextureRegion> imagesDrawable = new ArrayList<>();
	
	public ItemRendererSword(int sheetX, int sheetY) {
		for (int i = 0; i < 9; i++) {
			images.add(getImageFromSheet("items.png", sheetX + i, sheetY));
			imagesDrawable.add(ImageLoader.getImageFromSheet(
				"items.png",
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
