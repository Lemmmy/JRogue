package jr.dungeon.entities.decoration;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.entities.interfaces.ReadableEntity;
import jr.dungeon.events.EventHandler;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.Point;
import jr.utils.RandomUtils;

@Wishable(name="gravestone")
@Registered(id="entityGravestone")
public class EntityGravestone extends Entity implements ReadableEntity {
	private static final String[] GRAVE_MESSAGES = new String[] {
		"Rest in peace",
		"R.I.P.",
		// TODO: come up with some funny gravestone messages
	};
	
	@Expose private String message;
	
	public EntityGravestone(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
		
		message = RandomUtils.randomFrom(GRAVE_MESSAGES);
	}
	
	protected EntityGravestone() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.gravestone.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_GRAVESTONE;
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		// TODO: shit on the player's luck
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
	public void read(EntityLiving reader) {
		reader.getDungeon().log("The gravestone reads: '%s'", message);
	}
	
	@Override
	public boolean canRead(EntityLiving reader) {
		return true;
	}
}
