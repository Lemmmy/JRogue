package pw.lemmmy.jrogue.dungeon.entities.decoration;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.Entity;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.PassiveSoundEmitter;

public class EntityFountain extends Entity implements PassiveSoundEmitter {
	public EntityFountain(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public String getName(LivingEntity observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Fountain" : "fountain";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_FOUNTAIN;
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
	protected void onKick(LivingEntity kicker, boolean isPlayer, int dx, int dy) {}
	
	@Override
	protected void onWalk(LivingEntity walker, boolean isPlayer) {
		if (isPlayer) {
			getDungeon().log("There is a %s here.", getName(walker, false));
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
	
	@Override
	public float getSoundProbability() {
		return 0.15f;
	}
	
	@Override
	public String[] getSounds() {
		return new String[]{
			"You hear a light splashing sound.",
			"You hear a light splishing sound.",
			"You hear a light pattering sound.",
			"You hear the splashing of water.",
			"You hear the trickling of water.",
			"You hear the rushing of water.",
			"You hear bubbling water.",
			"You hear gushing water.",
			"You hear water falling on coins.",
			"You hear water pattering on coins.",
		};
	}
}
