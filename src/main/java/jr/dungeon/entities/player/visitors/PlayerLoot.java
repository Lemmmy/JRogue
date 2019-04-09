package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.interfaces.Lootable;
import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.utils.EntityHelper;
import jr.dungeon.events.ContainerShowEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerLoot implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		List<Entity> containerEntities = player.getLevel().entityStore.getEntitiesAt(player.getPosition())
			.filter(e -> !(e instanceof Player))
			.filter(EntityHelper::hasContainer)
			.collect(Collectors.toList());
		
		if (containerEntities.size() == 0) {
			player.getDungeon().log("There is nothing to loot here.");
			return;
		}
		
		player.getDungeon().turnSystem.turn();
		
		Entity containerEntity = containerEntities.get(0);

		if (containerEntity instanceof Lootable) {
			Lootable l = (Lootable)containerEntity;

			if (!l.isLootable()) {
				l.getLootFailedString().ifPresent(s -> player.getDungeon().log(s));
				return;
			}

			l.getLootSuccessString().ifPresent(s -> player.getDungeon().log(s));
			player.getDungeon().eventSystem.triggerEvent(new ContainerShowEvent(containerEntity));
		} else {
			player.getDungeon().yellowYou("can't loot that!");
		}
	}
}
