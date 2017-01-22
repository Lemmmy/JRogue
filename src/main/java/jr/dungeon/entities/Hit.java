package jr.dungeon.entities;

public class Hit {
	private final HitType hitType;
	private final int damage;
	
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
