package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;
import pw.lemmmy.jrogue.utils.Utils;

public abstract class Item {
	private int visualID;

	private boolean identified = false;
	private BUCStatus bucStatus = BUCStatus.UNCUSRED;

	public Item() {
		this.visualID = Utils.random(1000);
	}

	public int getVisualID() {
		return visualID;
	}

	public boolean isIdentified() {
		return identified;
	}

	public void setIdentified(boolean identified) {
		this.identified = identified;
	}

	public BUCStatus getBUCStatus() {
		return bucStatus;
	}

	public void setBUCStatus(BUCStatus bucStatus) {
		this.bucStatus = bucStatus;
	}

	public boolean isis() {
		return false;
	}

	public boolean beginsWithVowel() {
		return StringUtils.startsWithAny(getName(false, false), "a", "e", "i", "o", "u", "8");
	}

	public abstract String getName(boolean requiresCapitalisation, boolean plural);
	public abstract int getWeight();

	public abstract ItemAppearance getAppearance();

	public boolean equals(Item other) {
		return other.getClass() == getClass() && other.getAppearance() == getAppearance();
	}

	public enum BUCStatus {
		BLESSED,
		UNCUSRED,
		CURSED
	}
}
