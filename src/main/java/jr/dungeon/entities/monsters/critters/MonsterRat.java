package jr.dungeon.entities.monsters.critters;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.utils.RandomUtils;

import java.util.List;

@Registered(id="monsterRat")
public class MonsterRat extends Monster {
	@Expose private int speed;
	
	public MonsterRat(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);
		
		speed = Dungeon.NORMAL_SPEED + 4 - RandomUtils.random(8);
		
		StatefulAI ai = new StatefulAI(this);
		setAI(ai);
		ai.setDefaultState(new StateLurk(ai, 0));
	}
	
	protected MonsterRat() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.rat.clone(); // science
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
	public int getNutritionalValue() {
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
		return 9;
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
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
	public DamageType getMeleeDamageType() {
		return DamageType.RAT_BITE;
	}
	
	@Override
	public Verb getMeleeAttackVerb(EntityLiving victim) {
		return Lexicon.bite.clone();
	}
}
