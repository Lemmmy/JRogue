package pw.lemmmy.jrogue.rendering.gdx.entities;

import pw.lemmmy.jrogue.dungeon.entities.Appearance;

public enum EntityMap {
	;

	public static final int ENTITY_WIDTH = 16;
	public static final int ENTITY_HEIGHT = 16;

	private EntityRenderer renderer;

	EntityMap(EntityRenderer renderer) {
		this.renderer = renderer;
	}

	EntityMap(int sheetX, int sheetY) {
		this("tiles.png", sheetX, sheetY);
	}

	EntityMap(String sheetName, int sheetX, int sheetY) {
		this.renderer = new EntityRendererBasic(sheetName, sheetX, sheetY);
	}

	public Appearance getAppearance() {
		return Appearance.valueOf(name());
	}

	public EntityRenderer getRenderer() {
		return renderer;
	}
}
