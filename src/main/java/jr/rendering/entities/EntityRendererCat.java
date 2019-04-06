package jr.rendering.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.monsters.familiars.Familiar;
import jr.rendering.assets.Assets;
import jr.rendering.utils.ImageUtils;

import static jr.rendering.assets.Textures.entityFile;

public class EntityRendererCat extends EntityRendererBasic {
	private static final int imageCount = 12;
	private static final int ageCount = 3;
	private static final int breedCount = imageCount / ageCount;
	
	private TextureRegion[] images = new TextureRegion[imageCount];
	
	public EntityRendererCat() {
		super(null);
	}
	
	@Override
	public void onLoad(Assets assets) {
		super.onLoad(assets);
		
		assets.textures.loadPacked(entityFile("cats"), t -> ImageUtils.loadSheet(t, images, imageCount, 1));
	}
	
	@Override
	public TextureRegion getTextureRegion(Dungeon dungeon, Entity entity) {
		if (entity instanceof Familiar) {
			int breed = entity.getVisualID() % breedCount;
			int age = ((Familiar) entity).getAge();
			return images[breed * ageCount + age];
		} else {
			return images[0];
		}
	}
}
