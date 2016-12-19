package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

import java.util.Optional;

public class EntityChest extends Entity {
	private Container container;

	public EntityChest(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);

		this.container = new Container(getName(true));
	}

	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Chest" : "chest";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_CHEST;
	}

	@Override
	public Optional<Container> getContainer() {
		return Optional.of(container);
	}

	@Override
	public boolean lootable() {
		return true;
	}

	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {

	}

	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
		getDungeon().log("There is a %s here.", getName(false));
	}

	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
}
