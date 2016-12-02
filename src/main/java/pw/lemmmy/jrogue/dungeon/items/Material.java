package pw.lemmmy.jrogue.dungeon.items;

public enum Material {
	WOOD(1, true),
	STONE(2),
	BRONZE(5),
	IRON(10),
	STEEL(15),
	SILVER(25),
	GOLD(40),
	MITHRIL(50),
	ADAMANTITE(60);

	private boolean flammable = false;
	private int value;

	Material(int value) {
		this.value = value;
	}

	Material(int value, boolean flammable) {
		this.value = value;
		this.flammable = flammable;
	}

	public boolean isFlammable() {
		return flammable;
	}

	public int getValue() {
		return value;
	}
}
