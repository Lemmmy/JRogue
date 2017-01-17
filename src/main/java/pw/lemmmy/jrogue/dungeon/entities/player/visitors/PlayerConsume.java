package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.NutritionState;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.comestibles.ItemComestible;

public class PlayerConsume implements PlayerVisitor {
	private ItemComestible item;
	
	public PlayerConsume(ItemComestible item) {
		this.item = item;
	}
	
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
