package jr.dungeon.serialisation;

/**
 * Indicates that this class is serialisable via the {@link DungeonRegistries} system.
 */
public interface Serialisable {
	/**
	 * Called on unserialisation, as unserialisation does <b>not</b> call constructors.
	 */
	default void onUnserialise() {}
}
