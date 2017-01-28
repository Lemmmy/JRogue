package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.NutritionState;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.comestibles.ItemComestible;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerConsume implements PlayerVisitor {
	private ItemComestible item;
	
	@Override
	public void visit(Player player) {
		if (item.getTurnsRequiredToEat() == 1) {
			player.getDungeon().greenYou("eat the %s.", item.getName(player, false, false));
			player.setNutrition(player.getNutrition() + item.getNutrition());
			
			item.eatPart();
			return;
		}
		
		if (item.getEatenState() != ItemComestible.EatenState.EATEN) {
			if (item.getTurnsEaten() == item.getTurnsRequiredToEat() - 1) {
				player.getDungeon().greenYou("finish eating the %s.", item.getName(player, false, false));
				
				player.setNutrition(
					(int) (player.getNutrition() + Math.ceil(item.getNutrition() / item.getTurnsRequiredToEat()))
				);
				
				if (item.getStatusEffects(player) != null) {
					item.getStatusEffects(player).forEach(player::addStatusEffect);
				}
			} else {
				player.getDungeon().You("eat a part of the %s.", item.getName(player, false, false));
				
				player.setNutrition(
					(int) (player.getNutrition() + Math.floor(item.getNutrition() / item.getTurnsRequiredToEat()))
				);
				
				if (
					item.getTurnsEaten() == 0 &&
					item.getStatusEffects(player) != null &&
					item.getStatusEffects(player).size() >= 1 &&
					player.getNutritionState() != NutritionState.STARVING &&
					player.getNutritionState() != NutritionState.FAINTING &&
					player.getAttributes().getAttribute(Attribute.WISDOM) > 6
				) {
					player.getDungeon().You("feel funny - it might not be a good idea to continue eating.");
				}
			}
		}
		
		item.eatPart();
	}
}
