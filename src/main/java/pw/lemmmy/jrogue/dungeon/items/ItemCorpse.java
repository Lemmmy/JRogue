package pw.lemmmy.jrogue.dungeon.items;

import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

public class ItemCorpse extends Item {
	private LivingEntity entity;

	public ItemCorpse(LivingEntity entity) {
		this.entity = entity;
	}

	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return entity.getName(requiresCapitalisation) + " corpse" + (plural ? "s" : "");
	}

	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_CORPSE;
	}
}
