package pw.lemmmy.jrogue.dungeon.entities.projectiles;

import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.EntityTurnBased;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.tiles.TileType;

public abstract class EntityProjectile extends EntityTurnBased {
    private int dx = 0, dy = 0;
    private int range = Integer.MAX_VALUE;
    private int distanceTravelled = 0;

    private Entity source = null;

    public EntityProjectile(Dungeon dungeon, Level level, int x, int y) {
        super(dungeon, level, x, y);
    }
    
    public int getDeltaX() {
        return dx;
    }
    
    public int getDeltaY() {
        return dy;
    }
    
    public void setTravelDirection(int dx, int dy) {
        if (dx > 0) dx = 1;
        if (dy > 0) dy = 1;
        if (dx < 0) dx = -1;
        if (dy < 0) dy = -1;

        this.dx = dx;
        this.dy = dy;
    }

    public void setTravelRange(int range) {
        this.range = range;
    }

    public void setSource(Entity source) {
        this.source = source;
    }

    public Entity getSource() {
        return source;
    }

    public int getDistanceTravelled() {
        return distanceTravelled;
    }

    @Override
    public int getMovementSpeed() {
        return Dungeon.NORMAL_SPEED;
    }

    @Override
    public void move() {
        int x = getX() + dx;
        int y = getY() + dy;

        if (getLevel().getTile(x, y).getType().getSolidity() != TileType.Solidity.SOLID) {
            setPosition(x, y);
            distanceTravelled++;

            getLevel().getEntitiesAt(x, y).forEach(this::onHitEntity);

            if (distanceTravelled > range) {
                getLevel().removeEntity(this);
            }
        } else {
            getLevel().removeEntity(this);
        }
    }

    @Override
    public void applyMovementPoints() {
        setMovementPoints(getMovementPoints() + getMovementSpeed());
    }

    public void onHitEntity(Entity victim) {

    }

    @Override
    public void serialise(JSONObject obj) {
        super.serialise(obj);

        obj.put("dx", dx);
        obj.put("dy", dy);
        obj.put("range", range);
        obj.put("distanceTravelled", distanceTravelled);
    }

    @Override
    public void unserialise(JSONObject obj) {
        super.unserialise(obj);

        dx = obj.optInt("dx", 0);
        dy = obj.optInt("dy", 0);
        range = obj.optInt("range", 0);
        distanceTravelled = obj.optInt("distanceTravelled", 0);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public abstract String getName(boolean requiresCapitalisation);

    @Override
    public abstract EntityAppearance getAppearance();

    @Override
    protected abstract void onKick(LivingEntity kicker, boolean isPlayer, int x, int y);

    @Override
    protected abstract void onWalk(LivingEntity walker, boolean isPlayer);

    @Override
    public boolean canBeWalkedOn() {
        return true;
    }
}
