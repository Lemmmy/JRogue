package jr.dungeon.entities.monsters.ai;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.EntityReference;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMove;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventListener;
import jr.dungeon.serialisation.HasRegistry;
import jr.dungeon.serialisation.Serialisable;
import jr.dungeon.tiles.Solidity;
import jr.dungeon.tiles.TileType;
import jr.utils.DebugToStringStyle;
import jr.utils.Distance;
import jr.utils.Path;
import jr.utils.Point;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Artificial 'intelligence' class. Used to automate movement and actions of monsters in the dungeon, for example
 * making them try to follow the player or attack them.
 *
 * @see jr.dungeon.entities.monsters.ai.stateful.StatefulAI
 */
@HasRegistry
public abstract class AI implements Serialisable, EventListener {
    @NonNull @Getter @Setter private Monster monster;
    
    @Getter @Setter private AStarPathfinder pathfinder = new AStarPathfinder();
    @Expose private List<TileType> avoidTiles = new ArrayList<>();
    
    protected int suppressTurns = 0;
    
    public AI(Monster monster) {
        this.monster = monster;
        
        avoidTiles.add(TileType.TILE_TRAP);
    }
    
    protected AI() {} // deserialisation constructor
    
    @Override
    public void afterDeserialise() {
        pathfinder = new AStarPathfinder();
    }
    
    /**
     * Adds a tile to the list of tiles to avoid during pathfinding. The AI will absolutely never try to step on this
     * tile.
     *
     * @param tileType The tile type to add.
     */
    public void addAvoidTile(TileType tileType) {
        avoidTiles.add(tileType);
    }
    
    /**
     * @param point The position to check.
     *
     * @return Whether or not the AI can move to this position - checks if its inside the map and if its not solid.
     */
    public boolean canMoveTo(Point point) {
        return point.insideLevel(getLevel()) &&
            getLevel().tileStore.getTileType(point).getSolidity() != Solidity.SOLID;
    }

    /**
     * @return Whether or not the player is within the monster's {@link Monster#getVisibilityRange() visibility range}.
     */
    public boolean canMoveTowardsPlayer() {
        return distanceFromPlayer() < monster.getVisibilityRange();
    }
    
    /**
     * @param entity Entity
     *
     * @return Returns the linear distance between the entity and the monster.
     */
    public float distanceFrom(Entity entity) {
        return Distance.f(monster.getPosition(), entity.getPosition());
    }
    
    /**
     * @return Returns the linear distance between the player and the monster.
     */
    public float distanceFromPlayer() {
        return distanceFrom(monster.getDungeon().getPlayer());
    }
    
    /**
     * @param target The target to attack.
     *
     * @return Whether or not the monster is able to melee attack, and if it is adjacent to the target.
     */
    public boolean canMeleeAttack(EntityLiving target) {
        return monster.canMeleeAttack() && isAdjacentTo(target);
    }
    
    /**
     * @return Whether or not the monster is able to melee attack the player.
     */
    public boolean canMeleeAttackPlayer() {
        return canMeleeAttack(monster.getDungeon().getPlayer());
    }
    
    /**
     * @param target The target to check adjacency to.
     *
     * @return Whether or not the monster is adjacent to the target - checks for a
     * {@link Distance#chebyshev(int, int, int, int) Chebyshev distance} of 1 or less.
     */
    public boolean isAdjacentTo(EntityLiving target) {
        return Distance.chebyshev(target.getPosition(), monster.getPosition()) <= 1;
    }
    
    /**
     * @return Whether or not the monster is {#isAdjacentTo adjacent to} the player.
     */
    public boolean isAdjacentToPlayer() {
        Player player = monster.getDungeon().getPlayer();
        
        return isAdjacentTo(player);
    }
    
    /**
     * Melee attacks the target.
     *
     * @param target The target to attack.
     */
    public void meleeAttack(EntityLiving target) {
        monster.meleeAttack(target);
    }
    
    /**
     * Ranged attacks the target.
     *
     * @param target The target to attack.
     */
    public void rangedAttack(EntityLiving target) {
        monster.rangedAttack(target);
    }
    
    /**
     * Magic attacks the target.
     *
     * @param target The target to attack.
     */
    public void magicAttack(EntityLiving target) {
        monster.magicAttack(target);
    }
    
    /**
     * Melee attacks the player.
     */
    public void meleeAttackPlayer() {
        meleeAttack(monster.getDungeon().getPlayer());
    }
    
    /**
     * Ranged attacks the player.
     */
    public void rangedAttackPlayer() {
        rangedAttack(monster.getDungeon().getPlayer());
    }
    
    /**
     * Magic attacks the player.
     */
    public void magicAttackPlayer() {
        magicAttack(monster.getDungeon().getPlayer());
    }
    
    /**
     * {@link #moveTowards(Entity) Moves towards} the player.
     */
    public void moveTowardsPlayer() {
        Player player = monster.getDungeon().getPlayer();
        
        moveTowards(player);
    }
    
    /**
     * {@link #moveTowards(Point) Moves towards} the entity.
     *
     * @param entity The entity to {@link #moveTowards(Point) move towards.}
     */
    public void moveTowards(Entity entity) {
        moveTowards(entity.getPosition());
    }
    
    /**
     * {@link #moveTowards(Point) Moves towards} the {@link Entity} serialised within an {@link EntityReference}.
     *
     * @param entity The entity to {@link #moveTowards(Point) move towards.}
     */
    public void moveTowards(EntityReference entity) {
        if (!entity.isSet()) return;
        moveTowards(entity.get(getLevel()).getPosition());
    }
    
    /**
     * Finds a path towards the specified {@link Point} and move towards it.
     *
     * @param dest The position to move towards.
     *
     * @see AStarPathfinder
     */
    public void moveTowards(Point dest) {
        if (monster.hasAction()) return;
        
        Point src = monster.getPosition();
        
        Path path = pathfinder.findPath(
            monster.getLevel(),
            src,
            dest,
            monster.getVisibilityRange(),
            monster.canMoveDiagonally(),
            avoidTiles
        );
        
        if (path != null) {
            path.getSteps().stream()
                .filter(t -> !t.position.equals(src))
                .findFirst()
                .ifPresent(t -> monster.setAction(new ActionMove(t.position, new Action.NoCallback())));
        }
    }
    
    public boolean canReach(Entity entity) {
        Path path = pathfinder.findPath(
            monster.getLevel(),
            monster.getPosition(),
            entity.getPosition(),
            monster.getVisibilityRange(),
            monster.canMoveDiagonally(),
            avoidTiles
        );
        
        return path != null;
    }
    
    /**
     * Called every turn - this is the AI's turn to assign an action to the monster.
     */
    public abstract void update();
    
    @Override
    public Set<Object> getListenerSelves() {
        val selves = new HashSet<>();
        selves.add(this);
        selves.add(monster);
        return selves;
    }
    
    @Override
    public String toString() {
        return toStringBuilder().build();
    }
    
    public ToStringBuilder toStringBuilder() {
        return new ToStringBuilder(this, DebugToStringStyle.STYLE);
    }
    
    public Set<EventListener> getSubListeners() {
        return new HashSet<>();
    }
    
    public void suppress(int turns) {
        suppressTurns = turns;
    }
    
    /**
     * @return The {@link Monster}'s current {@link Dungeon}, or {@code null} if the monster is null.
     */
    public Dungeon getDungeon() {
        return getMonster() != null ? getMonster().getDungeon() : null;
    }
    
    /**
     * @return The {@link Monster}'s current {@link Level}, or {@code null} if the monster is null.
     */
    public Level getLevel() {
        return getMonster() != null ? getMonster().getLevel() : null;
    }
}
