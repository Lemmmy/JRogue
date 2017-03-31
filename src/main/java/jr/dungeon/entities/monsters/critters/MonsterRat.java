package jr.dungeon.entities.monsters.critters;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSourceType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.humanoid.StateLurk;
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
		
		StatefulAI ai = new StatefulAI(this);
		setAI(ai);
		ai.setDefaultState(new StateLurk(ai, 0));
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
	public void onKick(EntityKickedEntityEvent e) {
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
			damage(DamageSourceType.PLAYER_KICK, 1, e.getKicker());
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
			DamageSourceType.RAT_BITE,
			1,
			(Action.CompleteCallback) entity -> getDungeon().orangeThe("%s bites you!", getName(getDungeon().getPlayer(), false))
		));
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("speed", speed);
	}
}
