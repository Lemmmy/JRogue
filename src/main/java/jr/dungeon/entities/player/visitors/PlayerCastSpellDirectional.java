package jr.dungeon.entities.player.visitors;

import jr.dungeon.Prompt;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.magical.spells.Spell;
import jr.utils.RandomUtils;
import jr.utils.Utils;

public class PlayerCastSpellDirectional implements PlayerVisitor {
	private Spell spell;
	
	public PlayerCastSpellDirectional(Spell spell) {
		this.spell = spell;
	}
	
	@Override
	public void visit(Player player) {
		if (!player.canCastSpell(spell)) {
			player.getDungeon().redYou("don't have enough energy to cast that spell.");
			return;
		}
		
		String msg = "Cast in what direction?";
		
		player.getDungeon().prompt(new Prompt(msg, null, true, new Prompt.SimplePromptCallback(player.getDungeon()) {
			@Override
			public void onResponse(char response) {
				if (!Utils.MOVEMENT_CHARS.containsKey(response) &&
					spell.canCastAtSelf() && response != '5' && response != '.') {
					player.getDungeon().log(String.format("Invalid direction '[YELLOW]%s[]'.", response));
					return;
				}
				
				cast(response, player);
			}
		}));
	}
	
	private void cast(char response, Player player) {
		Integer[] d = response == '5' || response == '.' ?
					  new Integer[]{0, 0} :
					  Utils.MOVEMENT_CHARS.get(response);
		int dx = d[0];
		int dy = d[1];
		
		player.setNutrition(player.getNutrition() - spell.getNutritionCost());
		
		float successChance = spell.getSuccessChance(player) / 100f;
		
		if (RandomUtils.randomFloat() <= successChance) {
			player.setEnergy(player.getEnergy() - spell.getCastingCost());
			spell.castDirectional(player, dx, dy);
		} else {
			player.setEnergy((int) (player.getEnergy() - Math.floor(spell.getCastingCost() / 2)));
			player.getDungeon().orangeYou("fail to cast the spell correctly.");
		}
		
		player.getDungeon().turn();
	}
}
