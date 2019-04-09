package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.items.magical.DirectionType;
import jr.dungeon.items.magical.spells.Spell;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;
import jr.utils.VectorInt;

public class PlayerDefaultVisitors extends PlayerVisitorsContainer {
    public PlayerDefaultVisitors(Player player) {
        super(player);
    }
    
    public void teleport(Point position) {
        getPlayer().acceptVisitor(new PlayerTeleport(position));
    }
    
    public void walk(VectorInt direction) {
        getPlayer().acceptVisitor(new PlayerWalk(direction));
    }
    
    public void travelDirectional() {
        getPlayer().acceptVisitor(new PlayerTravelDirectional());
    }
    
    public void travelPathfind(Point position) {
        getPlayer().acceptVisitor(new PlayerTravelPathfind(position));
    }
    
    public void kick() {
        getPlayer().acceptVisitor(new PlayerKick());
    }
    
    public void castSpell(Spell spell) {
        if (spell.getDirectionType() == DirectionType.NON_DIRECTIONAL) {
            castSpellNonDirectional(spell);
        } else {
            castSpellDirectional(spell);
        }
    }
    
    private void castSpellNonDirectional(Spell spell) {
        getPlayer().acceptVisitor(new PlayerCastSpellNonDirectional(spell));
    }
    
    private void castSpellDirectional(Spell spell) {
        getPlayer().acceptVisitor(new PlayerCastSpellDirectional(spell));
    }
    
    public void eat() {
        getPlayer().acceptVisitor(new PlayerEat());
    }
    
    public void quaff() {
        getPlayer().acceptVisitor(new PlayerQuaff());
    }
    
    public void consume(ItemComestible item) {
        getPlayer().acceptVisitor(new PlayerConsume(item));
    }
    
    public void pickup() {
        getPlayer().acceptVisitor(new PlayerPickup());
    }
    
    public void drop() {
        getPlayer().acceptVisitor(new PlayerDrop());
    }
    
    public void loot() {
        getPlayer().acceptVisitor(new PlayerLoot());
    }
    
    public void wield() {
        getPlayer().acceptVisitor(new PlayerWield());
    }
    
    public void wield(Container.ContainerEntry containerEntry) {
        getPlayer().acceptVisitor(new PlayerWield(containerEntry));
    }
    
    public void fire() {
        // TODO: quiver
    }
    
    public void throwItem() {
        getPlayer().acceptVisitor(new PlayerThrowItem());
    }
    
    public void climbAny() {
        getPlayer().acceptVisitor(new PlayerClimbAny());
    }
    
    public void climbUp() {
        getPlayer().acceptVisitor(new PlayerClimbUp());
    }
    
    public void climbDown() {
        getPlayer().acceptVisitor(new PlayerClimbDown());
    }
    
    public void climb(Tile tile, boolean up) {
        getPlayer().acceptVisitor(new PlayerClimb(tile, up));
    }
    
    public void read() {
        getPlayer().acceptVisitor(new PlayerRead());
    }
}
