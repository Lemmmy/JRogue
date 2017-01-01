package pw.lemmmy.jrogue.dungeon.items.projectiles;

import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityArrow;
import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityProjectile;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;

public class ItemArrow extends ItemProjectile {
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		return (requiresCapitalisation ? "A" : "a") + "rrow" + (plural ? "s" : "");
	}
	
	@Override
	public float getWeight() {
		return 1;
	}
	
	@Override
	public ItemAppearance getAppearance() {
		return ItemAppearance.APPEARANCE_ARROW;
	}
	
	@Override
	public ItemCategory getCategory() {
		return ItemCategory.PROJECTILE;
	}
	
	@Override
	public Class<? extends EntityProjectile> getProjectileEntity() {
		return EntityArrow.class;
	}
}
