package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.items.magical.spells.Spell;
import jr.utils.RandomUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerCastSpellNonDirectional implements PlayerVisitor {
	private Spell spell;
	
	@Override
	public void visit(Player player) {
		if (!player.canCastSpell(spell)) {
			player.getDungeon().redYou("don't have enough energy to cast that spell.");
			return;
		}
		
		player.setNutrition(player.getNutrition() - spell.getNutritionCost());
		
		float successChance = spell.getSuccessChance(player) / 100f;
		
		if (RandomUtils.randomFloat() <= successChance) {
			player.setEnergy(player.getEnergy() - spell.getCastingCost());
			spell.castNonDirectional(player);
		} else {
			player.setEnergy((int) (player.getEnergy() - Math.floor(spell.getCastingCost() / 2)));
			player.getDungeon().orangeYou("fail to cast the spell correctly.");
		}
		
		player.getDungeon().turnSystem.turn(player.getDungeon());
	}
}
