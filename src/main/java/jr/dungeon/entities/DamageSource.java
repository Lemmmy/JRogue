package jr.dungeon.entities;

import jr.dungeon.items.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
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
	private DamageSourceType type;
}
