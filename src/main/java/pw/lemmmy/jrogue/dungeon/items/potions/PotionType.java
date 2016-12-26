package pw.lemmmy.jrogue.dungeon.items.potions;

import com.badlogic.gdx.graphics.Color;
import pw.lemmmy.jrogue.dungeon.entities.effects.Poison;

public enum PotionType {
    POTION_WATER(Color.CYAN, (entity, potency) -> {}),
    POTION_HEALTH(Color.RED, (ent, potency) -> ent.heal((int)Math.round(potency))),
    POTION_POISON(Color.LIME, new PotionEffectStatus(new Poison(4)));

    private final PotionEffect effect;
    private Color colour;

    PotionType(Color colour, PotionEffect effect) {
        this.colour = colour;
        this.effect = effect;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }
}
