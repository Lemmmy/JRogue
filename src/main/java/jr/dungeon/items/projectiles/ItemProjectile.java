package jr.dungeon.items.projectiles;

import jr.dungeon.entities.projectiles.EntityProjectile;
import jr.dungeon.items.Item;

public abstract class ItemProjectile extends Item {
    public abstract Class<? extends EntityProjectile> getProjectileEntity();
}
