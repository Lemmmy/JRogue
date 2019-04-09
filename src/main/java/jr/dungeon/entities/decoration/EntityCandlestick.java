package jr.dungeon.entities.decoration;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.entities.interfaces.Extinguishable;
import jr.dungeon.entities.interfaces.LightEmitter;
import jr.dungeon.events.EventHandler;
import jr.dungeon.serialisation.Registered;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Colour;
import jr.utils.Point;

@Registered(id="entityCandlestick")
public class EntityCandlestick extends Entity implements LightEmitter, Extinguishable {
	private static final int LIGHT_INTENSITY = 60;
	private static final Colour LIGHT_COLOUR = new Colour(0xFF9329FF);
	
	@Expose private boolean lit;
	
	public EntityCandlestick(Dungeon dungeon, Level level, Point position) {
		this(dungeon, level, position, true);
	}
	
	public EntityCandlestick(Dungeon dungeon, Level level, Point position, boolean isLit) {
		super(dungeon, level, position);
		lit = isLit;
	}
	
	protected EntityCandlestick() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.candlestick.clone();
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
	
	@EventHandler(selfOnly = true)
	public void onWalk(EntityWalkedOnEvent e) {
		if (e.isWalkerPlayer()) {
			getDungeon().log("There is %s here.", LanguageUtils.anObject(this));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public Colour getLightColour() {
		return LIGHT_COLOUR;
	}
	
	@Override
	public int getLightIntensity() {
		return lit ? LIGHT_INTENSITY : 0;
	}
}
