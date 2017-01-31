package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WishEffect implements Wish {
    private @Getter Class<? extends StatusEffect> eff;

    @Override
    public void grant(Dungeon dungeon, Player player, String... args) {
        try {
            player.addStatusEffect(eff.getConstructor().newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
