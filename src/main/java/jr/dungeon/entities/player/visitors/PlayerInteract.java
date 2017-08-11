package jr.dungeon.entities.player.visitors;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.interfaces.Interactive;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.DirectionPromptCallback;
import jr.dungeon.io.Prompt;
import jr.utils.VectorInt;

import java.util.List;
import java.util.Optional;

public class PlayerInteract implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Dungeon dungeon = player.getDungeon();
		
		dungeon.prompt(new Prompt("Interact in what direction?", null, true, new DirectionPromptCallback(dungeon) {
			@Override
			public void onDirectionResponse(VectorInt dir) {
				VectorInt target = player.getPositionVector().add(dir);
				List<Entity> entities = dungeon.getLevel().entityStore.getEntitiesAt(target.toPoint());
				
				Optional<Interactive> interactiveOpt = entities.stream()
					.filter(e -> e instanceof Interactive)
					.map(e -> (Interactive) e)
					.findFirst();
				
				if (!interactiveOpt.isPresent()) {
					dungeon.yellow("There's nothing to interact with there.");
					return;
				}
				
				interactiveOpt.get().interact(player);
			}
		}));
	}
}
