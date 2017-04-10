package jr.dungeon.entities;

import jr.dungeon.items.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DamageSource {
	/**
	 * The Entity that caused this damage. Can be null.
	 */
	private Entity attacker;
	
	/**
	 * The Item that caused this damage. Can be null.
	 */
	private Item item;
	
	/**
	 * The type of damage.
	 */
	private DamageType type = DamageType.UNKNOWN;
	
	public DamageSource() {}
	
	public DamageSource(Entity attacker) {
		this.attacker = attacker;
	}
	
	public DamageSource(Entity attacker, DamageType type) {
		this.attacker = attacker;
		this.type = type;
	}
	
	public DamageSource(Item item) {
		this.item = item;
	}
	
	public DamageSource(Item item, DamageType type) {
		this.item = item;
		this.type = type;
	}
	
	public DamageSource(Entity attacker, Item item) {
		this.attacker = attacker;
		this.item = item;
	}
	
	public DamageType.DamageClass getDamageClass() {
		return type.getDamageClass();
	}
}
