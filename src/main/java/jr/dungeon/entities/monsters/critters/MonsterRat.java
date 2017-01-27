package jr.dungeon.entities.monsters.critters;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.actions.EntityAction;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityKickedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.GhoulAI;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.DungeonEventHandler;
import jr.utils.RandomUtils;
import org.json.JSONObject;

import java.util.List;

public class MonsterRat extends Monster {
	private int speed;
	
	public MonsterRat(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);
		
		speed = Dungeon.NORMAL_SPEED + 4 - RandomUtils.random(8);
		
		setAI(new GhoulAI(this));
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Rat" : "rat";
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_RAT;
	}
	
	@Override
	public Size getSize() {
		return Size.SMALL;
	}
	
	@Override
	public int getMovementSpeed() {
		return speed;
	}
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public int getWeight() {
		return 75;
	}
	
	@Override
	public int getNutrition() {
		return 60;
	}
	
	@Override
	public float getCorpseChance() {
		return 0.1f;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 8;
	}
	
	@DungeonEventHandler(selfOnly = true)
	public void onKick(EntityKickedEvent e) {
		if (e.isKickerPlayer()) {
			getDungeon().You("kick the %s!", getName(e.getKicker(), false));
		}
				
		int damageChance = 2;
		
		if (e.isKickerPlayer()) {
			Player player = (Player) e.getKicker();
			int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
			damageChance = (int) Math.ceil(strength / 6) + 1;
		}
		
		if (RandomUtils.roll(1, damageChance) == 1) {
			damage(DamageSource.PLAYER_KICK, 1, e.getKicker());
		}
	}
	
	@Override
	public int getVisibilityRange() {
		return 10;
	}
	
	@Override
	public boolean canMoveDiagonally() {
		return true;
	}
	
	@Override
	public boolean canMeleeAttack() {
		return true;
	}
	
	@Override
	public boolean canRangedAttack() {
		return false;
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@Override
	public void meleeAttack(EntityLiving victim) {
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			DamageSource.RAT_BITE,
			1,
			(EntityAction.CompleteCallback) entity -> getDungeon().orangeThe("%s bites you!", getName(getDungeon().getPlayer(), false))
		));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("speed", speed);
	}
}
