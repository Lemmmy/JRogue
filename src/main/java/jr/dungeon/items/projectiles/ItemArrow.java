package jr.dungeon.items.projectiles;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.projectiles.EntityArrow;
import jr.dungeon.entities.projectiles.EntityProjectile;
import jr.dungeon.items.ItemAppearance;
import jr.dungeon.items.ItemCategory;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;

@Registered(id="itemArrow")
public class ItemArrow extends ItemProjectile {
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.arrow.clone();
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
