package jr.dungeon.entities.interfaces;

import jr.dungeon.entities.containers.Container;

import java.util.Optional;

/**
 * This interface is implemented by objects that can have a container associated
 * with them.
 */
public interface ContainerOwner {
	/**
	 * @return The container associated with this {@link ContainerOwner}.
	 */
	Optional<Container> getContainer();

	/**
	 * @return true if calling <code>getContainer</code> will definitely return a value.
	 */
	default boolean isContainerAvailable() {
		return getContainer().isPresent();
	}
}
