package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

public class EntityChest extends Entity {
	public EntityChest(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	@Override
	public String getName(boolean requiresCapitalisation) {
		return "Chest";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_CHEST;
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {

	}

	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
	}

	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
}
