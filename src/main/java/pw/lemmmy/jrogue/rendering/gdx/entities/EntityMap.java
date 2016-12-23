package pw.lemmmy.jrogue.rendering.gdx.entities;

import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;

public enum EntityMap {
	APPEARANCE_PLAYER(new EntityRendererPlayer(1, 0)),

	APPEARANCE_JACKAL(0, 2),
	APPEARANCE_FOX(1, 2),
	APPEARANCE_HOUND(2, 2),
	APPEARANCE_HELLHOUND(new EntityRendererParticleHound(3, 2, "hellhound")),
	APPEARANCE_ICEHOUND(new EntityRendererParticleHound(4, 2, "icehound")),
	APPEARANCE_SPIDER(new EntityRendererRandom(5, 2, 2)),
	APPEARANCE_RAT(new EntityRendererRandom(7, 2, 2)),
	APPEARANCE_SKELETON(9, 2),
	APPEARANCE_FISH_RED(new EntityRendererFish(1, 3)),
	APPEARANCE_FISH_ORANGE(new EntityRendererFish(2, 3)),
	APPEARANCE_FISH_YELLOW(new EntityRendererFish(3, 3)),
	APPEARANCE_FISH_GREEN(new EntityRendererFish(4, 3)),
	APPEARANCE_FISH_BLUE(new EntityRendererFish(5, 3)),
	APPEARANCE_FISH_PURPLE(new EntityRendererFish(6, 3)),
	APPEARANCE_PUFFERFISH(new EntityRendererFish(7, 3)),

	APPEARANCE_CHEST(new EntityRendererRandom(1, 1, 12)),
	APPEARANCE_FOUNTAIN(new EntityRendererFountain(13, 1, 2)),

	APPEARANCE_ITEM(new EntityRendererItem());

	public static final int ENTITY_WIDTH = 16;
	public static final int ENTITY_HEIGHT = 16;

	private EntityRenderer renderer;

	EntityMap(EntityRenderer renderer) {
		this.renderer = renderer;
	}

	EntityMap(int sheetX, int sheetY) {
		this("entities.png", sheetX, sheetY);
	}

	EntityMap(String sheetName, int sheetX, int sheetY) {
		this.renderer = new EntityRendererBasic(sheetName, sheetX, sheetY);
	}

	public EntityAppearance getAppearance() {
		return EntityAppearance.valueOf(name());
	}

	public EntityRenderer getRenderer() {
		return renderer;
	}
}
