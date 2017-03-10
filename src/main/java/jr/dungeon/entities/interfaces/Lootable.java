package jr.dungeon.entities.interfaces;

import java.util.Optional;

/**
 * An interface marking a class implementing it as being a lootable object.
 */
public interface Lootable {
	/**
	 * @return true if the object is lootable right now. Used, for example, to implement locking behaviour.
	 */
	default boolean isLootable() {
		return true;
	}

	/**
	 * @return The message shown when looting succeeds.
	 */
	default Optional<String> getLootSuccessString() {
		return Optional.empty();
	}

	/**
	 * @return The message shown when looting fails.
	 */
	default Optional<String> getLootFailedString() {
		return Optional.empty();
	}
}
