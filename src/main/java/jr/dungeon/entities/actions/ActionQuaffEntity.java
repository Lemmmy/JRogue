package jr.dungeon.entities.actions;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.interfaces.Quaffable;
import jr.dungeon.io.Messenger;

/**
 * Quaff/drink from entity action. For example, see {@link jr.dungeon.entities.decoration.EntityFountain}.
 *
 * @see Action
 */
public class ActionQuaffEntity extends Action {
    private final Quaffable quaffable;
    
    /**
     * Quaff/drink from entity action. For example, see {@link jr.dungeon.entities.decoration.EntityFountain}.
     *
     * @param quaffable The quaffable entity to quaff/drink from.
     * @param callback {@link Action.ActionCallback Callback} to call when action-related events occur.
     */
    public ActionQuaffEntity(Quaffable quaffable, ActionCallback callback) {
        super(callback);
        this.quaffable = quaffable;
    }
    
    @Override
    public void execute(Entity entity, Messenger msg) {
        runBeforeRunCallback(entity);
        if (entity instanceof EntityLiving) {
            quaffable.quaff((EntityLiving) entity);
        }
        runOnCompleteCallback(entity);
    }
}
