package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;

public abstract class Item {
	public abstract String getName(boolean requiresCapitalisation, boolean plural);

	public abstract ItemAppearance getAppearance();

	public boolean beginsWithVowel() {
		return StringUtils.startsWithAny(getName(false, false), "a", "e", "i", "o", "u", "8");
	}
}
