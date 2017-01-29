package jr.dungeon.events;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.containers.Container;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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
}
