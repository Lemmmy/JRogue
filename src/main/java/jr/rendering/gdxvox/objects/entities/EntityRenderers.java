package jr.rendering.gdxvox.objects.entities;

import jr.dungeon.entities.EntityAppearance;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererCandlestick;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererCat;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererPlayer;
import jr.rendering.gdxvox.objects.entities.renderers.EntityRendererTorch;

public class EntityRenderers {
	@SuppressWarnings("unchecked")
	@EntityRendererList
	public static void addRenderers(EntityRendererManager m) {
		m.addRenderers(new EntityRendererTorch(), EntityAppearance.APPEARANCE_TORCH);
		m.addRenderers(new EntityRendererCandlestick(), EntityAppearance.APPEARANCE_CANDLESTICK);
		m.addRenderers(new EntityRendererPlayer(), EntityAppearance.APPEARANCE_PLAYER);
		m.addRenderers(new EntityRendererCat(), EntityAppearance.APPEARANCE_TAMED_CAT);
	}
}
