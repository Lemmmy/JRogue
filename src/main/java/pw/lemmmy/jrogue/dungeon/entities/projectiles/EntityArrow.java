package pw.lemmmy.jrogue.dungeon.entities.projectiles;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;

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
    protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {

    }

    @Override
    protected void onWalk(LivingEntity walker, boolean isPlayer) {

    }
}
