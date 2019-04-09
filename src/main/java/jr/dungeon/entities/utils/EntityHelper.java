package jr.dungeon.entities.utils;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.interfaces.ContainerOwner;

import java.util.Optional;

/**
 * A class containing several static helper methods related to entities.
 */
public class EntityHelper {
    /**
     * @param ent Any entity.
     * @return Whether the specified entity has a container associated with it, and whether that container is available.
     */
    public static boolean hasContainer(Entity ent) {
        return ent instanceof ContainerOwner && ((ContainerOwner) ent).isContainerAvailable();
    }

    /**
     * @param ent Any entity with a container.
     * @return The container associated with this entity.
     * @see jr.dungeon.entities.utils.EntityHelper#hasContainer(Entity)
     */
    public static Optional<Container> getContainer(Entity ent) {
        if (ent instanceof ContainerOwner) {
            return ((ContainerOwner) ent).getContainer();
        }

        return Optional.empty();
    }
}
