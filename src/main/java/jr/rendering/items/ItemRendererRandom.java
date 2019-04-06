package jr.rendering.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.rendering.assets.Assets;
import jr.rendering.utils.ImageUtils;

import static jr.rendering.assets.Textures.itemFile;

public class ItemRendererRandom extends ItemRenderer {
	private String fileName;
	protected TextureRegion[] images;
	
	public ItemRendererRandom(String fileName, int count) {
		this.fileName = fileName;
		this.images = new TextureRegion[count];
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(itemFile(fileName), t -> ImageUtils.loadSheet(t, images, images.length, 1));
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, ItemStack itemStack, Item item, boolean reflect) {
		return images[item.getVisualID() % images.length];
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y, boolean reflect) {
		drawItem(batch, getTextureRegion(dungeon, itemStack, item, reflect), x, y, reflect);
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		return new TextureRegionDrawable(images[item.getVisualID() % images.length]);
	}
}
