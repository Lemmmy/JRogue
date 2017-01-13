package pw.lemmmy.jrogue.dungeon.entities.player.visitors;

import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.Spell;
import pw.lemmmy.jrogue.utils.RandomUtils;

public class PlayerCastSpellNonDirectional implements PlayerVisitor {
	private Spell spell;
	
	public PlayerCastSpellNonDirectional(Spell spell) {
		this.spell = spell;
	}
	
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
		
		player.getDungeon().turn();
	}
}
