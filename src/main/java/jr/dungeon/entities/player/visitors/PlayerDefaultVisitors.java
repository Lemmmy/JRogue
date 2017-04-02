package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.items.comestibles.ItemComestible;
import jr.dungeon.items.magical.spells.Spell;
import jr.dungeon.tiles.Tile;
import jr.utils.Point;

public class PlayerDefaultVisitors extends PlayerVisitorsContainer {
	public PlayerDefaultVisitors(Player player) {
		super(player);
	}
	
	public void teleport(int x, int y) {
		getPlayer().acceptVisitor(new PlayerTeleport(x, y));
	}

	public void teleport(Point p) {
		teleport(p.getX(), p.getY());
	}
	
	public void walk(int dx, int dy) {
		getPlayer().acceptVisitor(new PlayerWalk(dx, dy));
	}
	
	public void travelDirectional() {
		getPlayer().acceptVisitor(new PlayerTravelDirectional());
	}
	
	public void travelPathfind(int tx, int ty) {
		getPlayer().acceptVisitor(new PlayerTravelPathfind(tx, ty));
	}
	
	public void kick() {
		getPlayer().acceptVisitor(new PlayerKick());
	}
	
	public void castSpell(Spell spell) {
		switch (spell.getDirectionType()) {
			case NON_DIRECTIONAL:
				castSpellNonDirectional(spell);
				break;
			default:
				castSpellDirectional(spell);
				break;
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
