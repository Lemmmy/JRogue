package pw.lemmmy.jrogue.dungeon.entities.projectiles;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public class EntityArrow extends EntityProjectile {
    public EntityArrow(Dungeon dungeon, Level level, int x, int y) {
        super(dungeon, level, x, y);
    }

    @Override
    public String getName(boolean requiresCapitalisation) {
        return requiresCapitalisation ? "Arrow" : "arrow";
    }

    @Override
    public EntityAppearance getAppearance() {
        return EntityAppearance.APPEARANCE_ARROW;
    }

    @Override
    public void onHitEntity(Entity victim) {
        if (victim instanceof LivingEntity) {
            Entity source = getSource();

            if (source != null && source instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) victim;
                living.damage(DamageSource.ARROW, 1, (LivingEntity)source, source instanceof Player);
            }
        }
    }

    @Override
    protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {

    }

    @Override
    protected void onWalk(LivingEntity walker, boolean isPlayer) {

    }
}
