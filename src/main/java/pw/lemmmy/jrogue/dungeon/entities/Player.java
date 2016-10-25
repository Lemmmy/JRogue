package pw.lemmmy.jrogue.dungeon.entities;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;

public class Player extends LivingEntity {
	private String name;

	public Player(Dungeon dungeon, Level level, int x, int y, String name) {
		super(dungeon, level, x, y);

		this.name = name;
	}

	@Override
	public int getMaxHealth() {
		return 0;
	}

	@Override
	protected void onDamage(DamageSource damageSource, int damage) {

	}

	@Override
	protected void onDie(DamageSource damageSource) {
		getDungeon().You("die.");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Appearance getAppearance() {
		return Appearance.APPEARANCE_PLAYER;
	}

	@Override
	protected void move() {

	}

	@Override
	protected void onKick(Entity kicker) {
		getDungeon().You("step on your own foot.");
	}
}
