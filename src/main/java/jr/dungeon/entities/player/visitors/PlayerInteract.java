package jr.dungeon.entities.player.visitors;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionInteract;
import jr.dungeon.entities.interfaces.Interactive;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.Prompt;
import jr.language.transformers.Article;
import jr.language.transformers.Capitalise;
import jr.utils.Cardinal;
import jr.utils.Utils;
import jr.utils.VectorInt;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerInteract implements PlayerVisitor {
	@Override
	public void visit(Player player) {
		Dungeon dungeon = player.getDungeon();
		List<Entity> eligible = dungeon.getLevel().entityStore.getOctAdjacentEntities(player.getX(), player.getY()).stream()
			.filter(e -> e instanceof Interactive)
			.collect(Collectors.toList());
		
		switch (eligible.size()) {
			case 0: return;
			case 1: ((Interactive) eligible.get(0)).interact(player); break;
			default: {
				int eligibleSize = eligible.size();
				StringBuilder names = new StringBuilder();
				char[] options = new char[eligibleSize];
				
				for (int i = 0; i < eligibleSize; ++i) {
					Entity e = eligible.get(i);
					char c = Character.forDigit(i + 1, 10);
					String name = e.getName(player).build(Capitalise.all);
					
					int dir = Utils.asCardinalDirection(e.getPositionVector().sub(player.getPositionVector()));
					String dirName = Cardinal.nameOf(dir);
					names.append(c).append(" - ").append(name).append(" (").append(dirName).append(")");
					
					if (i < eligibleSize - 1) {
						names.append(", ");
					}
					
					options[i] = c;
				}
				
				dungeon.prompt(
					new Prompt("Interact with what? (" + names.toString() + ")", options, true,
						new Prompt.SimplePromptCallback(dungeon) {
							@Override
							public void onResponse(char response) {
								int i = Character.getNumericValue(response) - 1;
								player.setAction(new ActionInteract(
									new Action.NoCallback(),
									(Interactive) eligible.get(i)
								));
								dungeon.turnSystem.turn(dungeon);
							}
						}
					));
				break;
			}
		}
	}
}
