package jr.dungeon.entities.monsters.critters;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.Action;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.dungeon.language.Lexicon;
import jr.dungeon.language.Noun;
import jr.utils.RandomUtils;
import org.json.JSONObject;

import java.util.List;

public class MonsterSpider extends Monster {
	private int speed;
	
	public MonsterSpider(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);
		
		speed = Dungeon.NORMAL_SPEED - RandomUtils.random(6);
		
		StatefulAI ai = new StatefulAI(this);
		setAI(ai);
		ai.setDefaultState(new StateLurk(ai, 0));
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.spider.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_SPIDER;
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
		return 20;
	}
	
	@Override
	public int getNutritionalValue() {
		return 5;
	}
	
	@Override
	public float getCorpseChance() {
		return 0;
	}
	
	@Override
	public List<StatusEffect> getCorpseEffects(EntityLiving victim) {
		return null;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 9;
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		if (e.isKickerPlayer()) {
			getDungeon().You("step on the %s!", getName(e.getKicker(), false));
		}
		
		int damageChance = 2;
		
		if (e.isKickerPlayer()) {
			Player player = (Player) e.getKicker();
			int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
			damageChance = (int) Math.ceil(strength / 6) + 1;
		}
		
		if (RandomUtils.roll(1, damageChance) == 1) {
			damage(new DamageSource(e.getKicker(), null, DamageType.PLAYER_KICK), 1);
		}
	}
	
	@Override
	public int getVisibilityRange() {
		return 6;
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
		return false; // TODO: Web spinning & shooting
	}
	
	@Override
	public boolean canMagicAttack() {
		return false;
	}
	
	@Override
	public DamageType getMeleeDamageType() {
		return DamageType.SPIDER_BITE;
	}
	
	@Override
	public String getMeleeAttackString(EntityLiving victim) {
		return "The %s bites %s!";
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		obj.put("speed", speed);
	}
}
