package jr.rendering.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.rendering.assets.Assets;

public class ItemRendererBasic extends ItemRenderer {
	private TextureRegion image;
	private TextureRegion imageDrawable;
	
	private String fileName;
	
	public ItemRendererBasic(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(itemFile(fileName), t -> image = t);
		assets.textures.loadPacked(itemFile(fileName), t -> {
			imageDrawable = new TextureRegion(t);
			imageDrawable.flip(false, false);
			// TODO: it seems that all entities and tiles are flipped, but items when drawn in the GUI are not.
			//       the new asset system currently does not flip any textureregions when packed, so this could be
			//       problematic.
		});
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, ItemStack itemStack, Item item, boolean reflect) {
		return image;
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y, boolean reflect) {
		drawItem(batch, getTextureRegion(dungeon, itemStack, item, reflect), x, y, reflect);
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		return new TextureRegionDrawable(imageDrawable);
	}
}
