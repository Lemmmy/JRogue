package jr.dungeon.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.Container;

public class ContainerShowEvent extends DungeonEvent {
	private Entity containerEntity;
	private Container container;
	
	public ContainerShowEvent(Entity containerEntity) {
		this.containerEntity = containerEntity;
		
		containerEntity.getContainer().ifPresent(c -> container = c);
	}
	
	public ContainerShowEvent(Container container) {
		this.container = container;
	}
	
	public ContainerShowEvent(Entity containerEntity, Container container) {
		this.containerEntity = containerEntity;
		this.container = container;
	}
	
	public Entity getContainerEntity() {
		return containerEntity;
	}
	
	public Container getContainer() {
		return container;
	}
}
