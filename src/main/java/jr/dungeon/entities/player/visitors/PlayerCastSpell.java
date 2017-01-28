package jr.dungeon.entities.player.visitors;

import jr.dungeon.entities.player.Player;
import jr.dungeon.items.magical.spells.Spell;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerCastSpell implements PlayerVisitor {
	private Spell spell;
	
	@Override
	public void visit(Player player) {
		switch (spell.getDirectionType()) {
			case NON_DIRECTIONAL:
				player.acceptVisitor(new PlayerCastSpellNonDirectional(spell));
				break;
			default:
				player.acceptVisitor(new PlayerCastSpellDirectional(spell));
				break;
		}
	}
}
