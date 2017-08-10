package jr.rendering.entities;

import jr.dungeon.entities.EntityAppearance;

public enum EntityMap {
	APPEARANCE_PLAYER(new EntityRendererPlayer(1, 0)),
	
	APPEARANCE_TAMED_CAT(new EntityRendererCat()),
	
	APPEARANCE_JACKAL(0, 2),
	APPEARANCE_FOX(1, 2),
	APPEARANCE_HOUND(2, 2),
	APPEARANCE_HELLHOUND(new EntityRendererParticleHound(3, 2, "hellhound")),
	APPEARANCE_ICEHOUND(new EntityRendererParticleHound(4, 2, "icehound")),
	APPEARANCE_SPIDER(new EntityRendererRandom(5, 2, 2)),
	APPEARANCE_RAT(new EntityRendererRandom(7, 2, 2)),
	APPEARANCE_LIZARD(new EntityRendererRandom(19, 2, 3)),
	APPEARANCE_SKELETON(9, 2),
	APPEARANCE_GOBLIN(15, 2),
	APPEARANCE_GOBLIN_ZOMBIE(23, 2),
	APPEARANCE_MOLD_RED(10, 2),
	APPEARANCE_MOLD_YELLOW(11, 2),
	APPEARANCE_MOLD_GREEN(12, 2),
	APPEARANCE_MOLD_BLUE(13, 2),
	APPEARANCE_FISH_RED(new EntityRendererFish(1, 3)),
	APPEARANCE_FISH_ORANGE(new EntityRendererFish(2, 3)),
	APPEARANCE_FISH_YELLOW(new EntityRendererFish(3, 3)),
	APPEARANCE_FISH_GREEN(new EntityRendererFish(4, 3)),
	APPEARANCE_FISH_BLUE(new EntityRendererFish(5, 3)),
	APPEARANCE_FISH_PURPLE(new EntityRendererFish(6, 3)),
	APPEARANCE_PUFFERFISH(new EntityRendererFish(7, 3)),
	
	APPEARANCE_CHEST(new EntityRendererRandom(1, 1, 12)),
	APPEARANCE_FOUNTAIN(new EntityRendererFountain(13, 1, 2)),
	APPEARANCE_FOUNTAIN_FROZEN(new EntityRendererRandom(21, 1, 2)),
	APPEARANCE_CANDLESTICK(new EntityRendererCandlestick(15, 1)),
	APPEARANCE_CANDLESTICK_EXTINGUISHED(new EntityRendererCandlestick(15, 1)),
	APPEARANCE_WEAPON_RACK(16, 1),
	APPEARANCE_WEAPON_RACK_STOCKED(17, 1),
	APPEARANCE_ALTAR(20, 1),
	APPEARANCE_GRAVESTONE(new EntityRendererRandom(23, 1, 3)),
	
	APPEARANCE_ITEM(new EntityRendererItem()),
	
	APPEARANCE_ARROW(new EntityRendererProjectile("textures/entities.png", 1, 4)),
	APPEARANCE_STRIKE(new EntityRendererProjectile("textures/entities.png", 0, 4)),
	
	APPEARANCE_LIGHT_ORB(2, 4);
	
	public static final int ENTITY_WIDTH = 16;
	public static final int ENTITY_HEIGHT = 16;
	
	private EntityRenderer renderer;
	
	EntityMap(EntityRenderer renderer) {
		this.renderer = renderer;
	}
	
	EntityMap(int sheetX, int sheetY) {
		this("textures/entities.png", sheetX, sheetY);
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
	
	public static EntityRenderer getRenderer(EntityAppearance appearance) {
		return valueOf(appearance.name()).getRenderer();
	}
}
