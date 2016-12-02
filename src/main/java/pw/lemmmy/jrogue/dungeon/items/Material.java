package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;

public enum Material {
	WOOD(1, 2, true),
	STONE(2, 3),
	BRONZE(5, 4),
	IRON(10, 5),
	STEEL(15, 6),
	SILVER(25, 7),
	GOLD(40, 8),
	MITHRIL(50, 10),
	ADAMANTITE(60, 12);

	private int value;
	private int baseDamage;
	private boolean flammable = false;

	Material(int value, int baseDamage) {
		this.value = value;
		this.baseDamage = baseDamage;
	}

	Material(int value, int baseDamage, boolean flammable) {
		this.value = value;
		this.baseDamage = baseDamage;
		this.flammable = flammable;
	}

	public int getValue() {
		return value;
	}

	public int getBaseDamage() {
		return baseDamage;
	}

	public boolean isFlammable() {
		return flammable;
	}

	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? StringUtils.capitalize(this.name().toLowerCase()) : this.name().toLowerCase();
	}
}
