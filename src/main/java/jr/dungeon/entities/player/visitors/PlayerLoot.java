package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.entities.Entity;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerLoot implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		List<Entity> containerEntities = player.getLevel().entityStore.getEntitiesAt(player.getX(), player.getY()).stream()
			.filter(e -> !(e instanceof Player) && e.getContainer().isPresent())
			.collect(Collectors.toList());
		
		if (containerEntities.size() == 0) {
			player.getDungeon().log("There is nothing to loot here.");
			return;
		}
		
		player.getDungeon().turn();
		
		Entity containerEntity = containerEntities.get(0);
		
		if (!containerEntity.lootable()) {
			containerEntity.lootFailedString().ifPresent(s -> player.getDungeon().log(s));
			return;
		}
		
		containerEntity.lootSuccessString().ifPresent(s -> player.getDungeon().log(s));
		player.getDungeon().showContainer(containerEntity);
	}
}
