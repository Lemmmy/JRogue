package pw.lemmmy.jrogue.dungeon.entities.actions;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.items.ItemDrinkable;

public class ActionDrink extends EntityAction {
    private final ItemDrinkable drinkable;

    public ActionDrink(Dungeon dungeon, Entity entity, ItemDrinkable item) {
        this(dungeon, entity, item, null);
    }

    public ActionDrink(Dungeon dungeon, Entity entity, ItemDrinkable item, ActionCallback callback) {
        super(dungeon, entity, callback);

        this.drinkable = item;
    }

    @Override
    public void execute() {
        runBeforeRunCallback();
        drinkable.drink(getEntity());
        runOnCompleteCallback();
    }
}
