package jr.rendering.entities;

import jr.dungeon.entities.EntityAppearance;

public enum EntityMap {
	APPEARANCE_PLAYER(new EntityRendererPlayer()),
	
	APPEARANCE_TAMED_CAT(new EntityRendererCat()),
	
	APPEARANCE_JACKAL("jackal"),
	APPEARANCE_FOX("fox"),
	APPEARANCE_HOUND("hound"),
	APPEARANCE_HELLHOUND(new EntityRendererParticleHound("hellhound", "hellhound")),
	APPEARANCE_ICEHOUND(new EntityRendererParticleHound("icehound", "icehound")),
	APPEARANCE_SPIDER(new EntityRendererRandom("spiders", 2)),
	APPEARANCE_RAT(new EntityRendererRandom("rats", 2)),
	APPEARANCE_LIZARD(new EntityRendererRandom("lizards", 3)),
	APPEARANCE_SKELETON("skeleton"),
	APPEARANCE_GOBLIN("goblin"),
	APPEARANCE_GOBLIN_ZOMBIE("zombie_goblin"),
	APPEARANCE_MOLD_RED("mold_red"),
	APPEARANCE_MOLD_YELLOW("mold_yellow"),
	APPEARANCE_MOLD_GREEN("mold_green"),
	APPEARANCE_MOLD_BLUE("mold_blue"),
	APPEARANCE_FISH_RED("fish_red"),
	APPEARANCE_FISH_ORANGE("fish_orange"),
	APPEARANCE_FISH_YELLOW("fish_yellow"),
	APPEARANCE_FISH_GREEN("fish_green"),
	APPEARANCE_FISH_BLUE("fish_blue"),
	APPEARANCE_FISH_PURPLE("fish_purple"),
	APPEARANCE_PUFFERFISH("pufferfish"),
	
	APPEARANCE_CHEST(new EntityRendererRandom("chests", 12)),
	APPEARANCE_FOUNTAIN(new EntityRendererFountain("fountains", 2)),
	APPEARANCE_FOUNTAIN_FROZEN(new EntityRendererRandom("fountains_frozen", 2)),
	APPEARANCE_CANDLESTICK(new EntityRendererCandlestick("candlestick")),
	APPEARANCE_CANDLESTICK_EXTINGUISHED("candlestick"),
	APPEARANCE_WEAPON_RACK("weapon_rack_empty"),
	APPEARANCE_WEAPON_RACK_STOCKED("weapon_rack"),
	APPEARANCE_ALTAR("altar"),
	APPEARANCE_GRAVESTONE(new EntityRendererRandom("gravestones", 3)),
	
	APPEARANCE_ITEM(new EntityRendererItem()),
	
	APPEARANCE_ARROW(new EntityRendererProjectile("projectile_arrow")),
	APPEARANCE_STRIKE(new EntityRendererProjectile("projectile_strike")),
	
	APPEARANCE_LIGHT_ORB("light_orb");
	
	public static final int ENTITY_WIDTH = 16;
	public static final int ENTITY_HEIGHT = 16;
	
	private EntityRenderer renderer;
	
	EntityMap(EntityRenderer renderer) {
		this.renderer = renderer;
	}
	
	EntityMap(String fileName) {
		this.renderer = new EntityRendererBasic(fileName);
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
