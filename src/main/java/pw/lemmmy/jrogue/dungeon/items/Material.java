package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;

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

	Material(int value, int baseDamage, int levelRequiredToFind) {
		this.value = value;
		this.baseDamage = baseDamage;
		this.levelRequiredToSpawn = levelRequiredToFind;
	}

	Material(int value, int baseDamage, int levelRequiredToFind, boolean flammable) {
		this.value = value;
		this.baseDamage = baseDamage;
		this.levelRequiredToSpawn = levelRequiredToFind;
		this.flammable = flammable;
	}

	public int getValue() {
		return value;
	}

	public int getBaseDamage() {
		return baseDamage;
	}

	public int getLevelRequiredToSpawn() {
		return levelRequiredToSpawn;
	}

	public boolean isFlammable() {
		return flammable;
	}

	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? StringUtils.capitalize(this.name().toLowerCase()) : this.name().toLowerCase();
	}
}
