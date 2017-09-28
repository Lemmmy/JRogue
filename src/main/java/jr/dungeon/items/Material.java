package jr.dungeon.items;

import lombok.Getter;

@Getter
public enum Material {
	WOOD(1, 2, 0, true),
	STONE(2, 3, 0),
	BRONZE(5, 4, 2),
	IRON(10, 5, 6),
	STEEL(15, 6, 8),
	SILVER(25, 7, 12),
	GOLD(40, 8, 14),
	MITHRIL(50, 10, 16),
	ADAMANTITE(60, 12, 20);
	
	private int value;
	private int baseDamage;
	private int levelRequiredToSpawn;
	private boolean flammable = false;
	
	Material(int value, int baseDamage, int levelRequiredToSpawn) {
		this.value = value;
		this.baseDamage = baseDamage;
		this.levelRequiredToSpawn = levelRequiredToSpawn;
	}
	
	Material(int value, int baseDamage, int levelRequiredToSpawn, boolean flammable) {
		this.value = value;
		this.baseDamage = baseDamage;
		this.levelRequiredToSpawn = levelRequiredToSpawn;
		this.flammable = flammable;
	}
	
	public String getName() {
		return this.name().toLowerCase();
	}
}
