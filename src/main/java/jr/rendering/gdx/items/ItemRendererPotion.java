package jr.rendering.gdx.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.quaffable.potions.ItemPotion;
import jr.rendering.gdx.utils.CompositeDrawable;
import jr.rendering.gdx.utils.ImageLoader;

public class ItemRendererPotion extends ItemRenderer {
	private final TextureRegion fluidTex, bottleTex;
	
	private final TextureRegionDrawable fluidDrawable, bottleDrawable;
	
	public ItemRendererPotion(int sheetX, int sheetY, int liquidX, int liquidY) {
		bottleTex = ImageLoader.getImageFromSheet("items.png", sheetX, sheetY);
		fluidTex = ImageLoader.getImageFromSheet("items.png", liquidX, liquidY);
		
		fluidDrawable = new TextureRegionDrawable(ImageLoader
			.getImageFromSheet("items.png", liquidX, liquidY, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false));
		bottleDrawable = new TextureRegionDrawable(ImageLoader
			.getImageFromSheet("items.png", sheetX, sheetY, ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT, false));
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y) {
		if (item instanceof ItemPotion) {
			ItemPotion potion = (ItemPotion) item;
			Color prevColour = batch.getColor();
			batch.setColor(PotionColourMap.fromPotion(potion));
			batch.draw(fluidTex, x, y);
			batch.setColor(prevColour);
		}
		
		batch.draw(bottleTex, x, y);
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		if (item instanceof ItemPotion) {
			ItemPotion potion = (ItemPotion) item;
			return new CompositeDrawable(
				ItemMap.ITEM_WIDTH, ItemMap.ITEM_HEIGHT,
				fluidDrawable.tint(PotionColourMap.fromPotion(potion)), bottleDrawable
			);
		}
		
		return bottleDrawable;
	}
}
