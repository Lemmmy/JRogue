package jr.dungeon.items.projectiles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.projectiles.EntityArrow;
import jr.dungeon.entities.projectiles.EntityProjectile;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.ItemCategory;

public class ItemArrow extends ItemProjectile {
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
		
		s += (requiresCapitalisation ? "A" : "a") + "rrow" + (plural ? "s" : "");
		
		return s;
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
