package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;

public abstract class Item {
	private boolean identified = false;
	private BUCStatus bucStatus = BUCStatus.UNCUSRED;

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

	public enum BUCStatus {
		BLESSED,
		UNCUSRED,
		CURSED
	}
}
