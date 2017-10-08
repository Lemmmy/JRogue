package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererTorch;
import jr.rendering.gdxvox.objects.tiles.renderers.TileRendererWall;

public class EntityRenderers {
	@SuppressWarnings("unchecked")
	@EntityRendererList
	public static void addRenderers(EntityRendererMap m) {
		m.addRenderers(new EntityRendererTorch(), EntityAppearance.APPEARANCE_TORCH);
	}
}
