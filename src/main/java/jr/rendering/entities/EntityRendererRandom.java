package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.entities.Entity;
import jr.rendering.assets.Assets;
import jr.rendering.utils.ImageUtils;

import static jr.rendering.assets.Textures.entityFile;

public class EntityRendererRandom extends EntityRendererBasic {
	private String fileName;
	protected TextureRegion[] images;
	
	public EntityRendererRandom(String fileName, int count) {
		super(null);
		
		this.fileName = fileName;
		this.images = new TextureRegion[count];
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(entityFile(fileName), t -> ImageUtils.loadSheet(t, images, images.length, 1));
	}
	
	@Override
	public TextureRegion getTextureRegion(Entity entity) {
		return images[entity.getVisualID() % images.length];
	}
}
