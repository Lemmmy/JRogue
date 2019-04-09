package jr.dungeon.wishes;

import jr.dungeon.Dungeon;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.player.Player;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;

@AllArgsConstructor
public class WishEffect<T extends StatusEffect> implements Wish {
    private Class<T> effect;

    @Override
    public void grant(Dungeon dungeon, Player player, String... args) {
        try {
			Constructor<T> generalConstructor = ConstructorUtils.getAccessibleConstructor(effect);
			Constructor<T> durationConstructor = ConstructorUtils.getAccessibleConstructor(effect, int.class);
			
			int duration = args.length > 0 && args[0] != null ? Integer.parseInt(args[0]) : 0;
			
			if (generalConstructor == null || durationConstructor != null && duration != 0) {
				player.addStatusEffect(durationConstructor.newInstance(duration));
			} else {
				player.addStatusEffect(generalConstructor.newInstance());
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
