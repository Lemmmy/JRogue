package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerLoot implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		List<Entity> containerEntities = player.getLevel().getEntitiesAt(player.getX(), player.getY()).stream()
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
