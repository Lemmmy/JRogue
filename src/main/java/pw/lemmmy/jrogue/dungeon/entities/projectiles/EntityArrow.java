package pw.lemmmy.jrogue.dungeon.entities.projectiles;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.DamageSource;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;

public class EntityArrow extends EntityProjectile {
    private boolean canPenetrate = false;

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

    public void setCanPenetrate(boolean penetrate) {
        canPenetrate = penetrate;
    }

    @Override
    public void onHitEntity(Entity victim) {
        if (victim instanceof LivingEntity) {
            Entity source = getSource();

            if (source != null && source instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) victim;

                if (source instanceof Player) {
                    source.getDungeon().Your("arrow hits the %s.", living.getName(false));
                }

                if (living instanceof Player) {
                    living.getDungeon().You("You get hit by an arrow from %s." + source.getName(false));
                }

                living.damage(DamageSource.ARROW, 1, (LivingEntity)source, source instanceof Player);

                if (!canPenetrate) {
                    getLevel().removeEntity(this);
                }
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
