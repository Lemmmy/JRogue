package pw.lemmmy.jrogue.rendering.gdx.entities;

import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;

public enum EntityMap {
	APPEARANCE_PLAYER(new EntityRendererPlayer(1, 0)),

	APPEARANCE_JACKAL(0, 2),

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
