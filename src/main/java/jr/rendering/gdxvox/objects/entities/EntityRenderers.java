package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.tiles.TileType;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererCandlestick;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererPlayer;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererTorch;
import jr.rendering.gdxvox.objects.tiles.renderers.TileRendererWall;

public class EntityRenderers {
	@SuppressWarnings("unchecked")
	@EntityRendererList
	public static void addRenderers(EntityRendererMap m) {
		m.addRenderers(new EntityRendererTorch(), EntityAppearance.APPEARANCE_TORCH);
		m.addRenderers(new EntityRendererCandlestick(), EntityAppearance.APPEARANCE_CANDLESTICK);
		m.addRenderers(new EntityRendererPlayer(), EntityAppearance.APPEARANCE_PLAYER);
	}
}
