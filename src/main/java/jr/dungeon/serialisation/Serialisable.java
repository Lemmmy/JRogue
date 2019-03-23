package jr.dungeon.serialisation;

/**
 * Indicates that this class is serialisable via the {@link DungeonRegistries} system.
 */
public interface Serialisable {
	/**
	 * Called just before serialisation.
	 */
	default void beforeSerialise() {}
	
	/**
	 * Called just after unserialisation, as deserialisation does <b>not</b> call constructors.
	 */
	default void afterDeserialise() {}
}
