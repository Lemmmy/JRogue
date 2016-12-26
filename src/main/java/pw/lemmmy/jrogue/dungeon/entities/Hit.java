package pw.lemmmy.jrogue.dungeon.entities;

public class Hit {
	private HitType hitType;
	private int damage;
	
	public Hit(HitType hitType, int damage) {
		this.hitType = hitType;
		this.damage = damage;
	}
	
	public HitType getHitType() {
		return hitType;
	}
	
	public int getDamage() {
		return damage;
	}
}
