package pw.lemmmy.jrogue.dungeon.entities.decoration;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.*;

import java.awt.*;

public class EntityCandlestick extends Entity implements LightEmitter, Extinguishable {
	private boolean lit = true;

	public EntityCandlestick(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	public EntityCandlestick(Dungeon dungeon, Level level, int x, int y, boolean isLit) {
		super(dungeon, level, x, y);
		lit = isLit;
	}
	
	@Override
	public void light() {
		lit = true;
	}
	
	@Override
	public void extinguish() {
		lit = false;
	}
	
	@Override
	public boolean isLit() {
		return lit;
	}
	
	@Override
	public void setLit(boolean lit) {
		this.lit = lit;
	}
	
	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Candlestick" : "candlestick";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return lit ? EntityAppearance.APPEARANCE_CANDLESTICK : EntityAppearance.APPEARANCE_CANDLESTICK_EXTINGUISHED;
	}
	
	@Override
	public int getDepth() {
		return 0;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@Override
	protected void onKick(LivingEntity kicker, boolean isPlayer, int x, int y) {}
	
	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
		getDungeon().log("There is a %s here.", getName(false));
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public Color getLightColour() {
		return new Color(0xFF9329);
	}
	
	@Override
	public int getLightIntensity() {
		return lit ? 100 : 0;
	}
}
