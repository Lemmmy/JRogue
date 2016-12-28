package pw.lemmmy.jrogue.dungeon.items.weapons;

import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.projectiles.EntityProjectile;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.skills.Skill;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;

import java.lang.reflect.Constructor;
import java.util.Optional;

public abstract class ItemProjectileEmitter<T extends EntityProjectile> extends ItemWeapon {
    private final Class<? extends T> projectileType;

    public ItemProjectileEmitter(Class<? extends T> projectileType) {
        this.projectileType = projectileType;
    }

    protected Optional<T> createProjectile(Entity emitter, int x, int y) {
        try {
            Constructor<? extends T> c = projectileType.getConstructor(Dungeon.class, Level.class, int.class, int.class);
            return Optional.of(
                    c.newInstance(emitter.getDungeon(), emitter.getLevel(), emitter.getX() + x, emitter.getY() + y));
        } catch (Exception e) {
            JRogue.getLogger().error("Couldn't create projectile entity", e);
        }

        return Optional.empty();
    }

    @Override
    public void hit(LivingEntity attacker, LivingEntity victim) {

    }

    @Override
    public void zap(LivingEntity attacker, LivingEntity victim, int dx, int dy) {

    }

    @Override
    public void fire(LivingEntity attacker, LivingEntity victim, int dx, int dy) {
        Optional<T> projectileOpt = createProjectile(attacker, 0, 0);

        if (!projectileOpt.isPresent()) {
            JRogue.getLogger().error("Failed to fire projectile!!");
            return;
        }

        EntityProjectile projectile = projectileOpt.get();
        projectile.update();
        attacker.getLevel().addEntity(projectile);
    }

    @Override
    public boolean isMelee() {
        return false;
    }

    @Override
    public boolean isRanged() {
        return true;
    }

    @Override
    public abstract boolean isTwoHanded();

    @Override
    public abstract boolean isMagic();

    @Override
    public abstract int getToHitBonus();

    @Override
    public abstract Skill getSkill();

    @Override
    public abstract String getName(boolean requiresCapitalisation, boolean plural);

    @Override
    public abstract float getWeight();

    @Override
    public abstract ItemAppearance getAppearance();
}
