package pw.lemmmy.jrogue.dungeon.items;

import org.apache.commons.lang3.StringUtils;

public class ItemGem extends Item {
	private Gem gem;
	private boolean worthless = false;

	public ItemGem(Gem gem, boolean worthless) {
		this.gem = gem;
		this.worthless = worthless;
	}

	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		String colour = gem.getAppearance().name().replace("APPEARANCE_GEM_", "").toLowerCase();

		if (isIdentified()) {
			if (worthless) {
				if (requiresCapitalisation) {
					return plural ?
						   "Worthless pieces of " + colour + " glass" :
						   "Worthless piece of " + colour + " " + "glass";
				} else {
					return plural ?
						   "worthless pieces of " + colour + " glass" :
						   "worthless piece of " + colour + " " + "glass";
				}
			} else {
				String gemName = plural ? gem.getName() : gem.getNamePlural();

				return requiresCapitalisation ? StringUtils.capitalize(gemName) : gemName;
			}
		} else {
			return String.format(
				"%s gem%s",
				requiresCapitalisation ? StringUtils.capitalize(colour) : colour,
				plural ? "s" : ""
			);
		}
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public ItemAppearance getAppearance() {
		return gem.getAppearance();
	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.GEMS;
	}

	public Gem getGem() {
		return gem;
	}

	public boolean isWorthless() {
		return worthless;
	}

	public enum Gem {
		// TODO: Gem values

		RED_GARNET(ItemAppearance.APPEARANCE_GEM_RED),
		RUBY(ItemAppearance.APPEARANCE_GEM_RED),

		GOLDEN_SAPPHIRE(ItemAppearance.APPEARANCE_GEM_ORANGE),
		TOPAZ(ItemAppearance.APPEARANCE_GEM_ORANGE, "topaz", "topazes"),

		CITRINE(ItemAppearance.APPEARANCE_GEM_YELLOW),
		YELLOW_DIAMOND(ItemAppearance.APPEARANCE_GEM_YELLOW),
		YELLOW_TOPAZ(ItemAppearance.APPEARANCE_GEM_YELLOW, "yellow topaz", "yellow topazes"),

		GREEN_AMETHYST(ItemAppearance.APPEARANCE_GEM_LIME),
		PERIDOT(ItemAppearance.APPEARANCE_GEM_LIME),

		EMERALD(ItemAppearance.APPEARANCE_GEM_GREEN),
		JADE(ItemAppearance.APPEARANCE_GEM_GREEN),
		GREEN_GARNET(ItemAppearance.APPEARANCE_GEM_GREEN),

		APATITE(ItemAppearance.APPEARANCE_GEM_CYAN),
		AQUAMARINE(ItemAppearance.APPEARANCE_GEM_CYAN),
		BLUE_DIAMOND(ItemAppearance.APPEARANCE_GEM_CYAN),
		SKY_BLUE_TOPAZ(ItemAppearance.APPEARANCE_GEM_CYAN, "sky blue topaz", "sky blue topazes"),

		BLUE_APATITE(ItemAppearance.APPEARANCE_GEM_BLUE),
		LAPIS_LAZULI(ItemAppearance.APPEARANCE_GEM_BLUE),
		SAPPHIRE(ItemAppearance.APPEARANCE_GEM_BLUE),

		ALEXANDRITE(ItemAppearance.APPEARANCE_GEM_PURPLE),
		AMETHYST(ItemAppearance.APPEARANCE_GEM_PURPLE),
		TANZANITE(ItemAppearance.APPEARANCE_GEM_PURPLE),

		DIAMOND(ItemAppearance.APPEARANCE_GEM_WHITE),
		QUARTZ(ItemAppearance.APPEARANCE_GEM_WHITE, "quartz", "quartzes"),
		WHITE_TOPAZ(ItemAppearance.APPEARANCE_GEM_WHITE),
		WHITE_SAPPHIRE(ItemAppearance.APPEARANCE_GEM_WHITE),

		BLACK_DIAMOND(ItemAppearance.APPEARANCE_GEM_BLACK),
		BLACK_ONYX(ItemAppearance.APPEARANCE_GEM_BLACK, "onyx", "onyxes"),
		DIOPSIDE(ItemAppearance.APPEARANCE_GEM_BLACK),
		HEMATITE(ItemAppearance.APPEARANCE_GEM_BLACK),
		SPINEL(ItemAppearance.APPEARANCE_GEM_BLACK);

		private ItemAppearance appearance;
		private String name;
		private String namePlural;

		Gem(ItemAppearance appearance) {
			this.appearance = appearance;
			name = name().toLowerCase().replace("_", "");
			namePlural = name + "s";
		}

		Gem(ItemAppearance appearance, String name, String namePlural) {
			this.appearance = appearance;
			this.name = name;
			this.namePlural = namePlural;
		}

		public ItemAppearance getAppearance() {
			return appearance;
		}

		public String getName() {
			return name;
		}

		public String getNamePlural() {
			return namePlural;
		}
	}
}
