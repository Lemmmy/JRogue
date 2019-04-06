package jr.rendering.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import jr.dungeon.Dungeon;
import jr.dungeon.items.HasMaterial;
import jr.dungeon.items.Item;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Material;
import jr.rendering.assets.Assets;

import java.util.HashMap;
import java.util.Map;

import static jr.rendering.assets.Textures.itemFile;

public class ItemRendererSword extends ItemRenderer {
	private String fileNamePrefix;
	protected Map<Material, TextureRegion> images = new HashMap<>();
	
	public ItemRendererSword(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		for (Material material : Material.values()) {
			String materialName = MaterialMap.valueOf(material.name()).getFileName();
			String fileName = fileNamePrefix + "_" + materialName;
			
			assets.textures.loadPacked(itemFile(fileName), t -> images.put(material, t));
		}
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, ItemStack itemStack, Item item, boolean reflect) {
		return getImageFromMaterial(((HasMaterial) item).getMaterial());
	}
	
	@Override
	public void draw(SpriteBatch batch, Dungeon dungeon, ItemStack itemStack, Item item, int x, int y, boolean reflect) {
		drawItem(batch, getTextureRegion(dungeon, itemStack, item, reflect), x, y, reflect);
	}
	
	private TextureRegion getImageFromMaterial(Material material) {
		return images.get(material);
	}
	
	@Override
	public Drawable getDrawable(ItemStack itemStack, Item item) {
		return new TextureRegionDrawable(getImageFromMaterial(((HasMaterial) item).getMaterial()));
	}
	
}
