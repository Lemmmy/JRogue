package jr.dungeon.entities.monsters.canines;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.DamageType;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.effects.InjuredFoot;
import jr.dungeon.entities.effects.StrainedLeg;
import jr.dungeon.entities.events.EntityDamagedEvent;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.StatefulAI;
import jr.dungeon.entities.monsters.ai.stateful.generic.StateLurk;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.Verb;
import jr.language.transformers.Article;
import jr.language.transformers.Capitalise;
import jr.utils.RandomUtils;

public abstract class MonsterCanine extends Monster {
	public MonsterCanine(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);
		
		StatefulAI ai = new StatefulAI(this);
		setAI(ai);
		ai.setDefaultState(new StateLurk(ai, 0));
	}
	
	protected MonsterCanine() { super(); }
	
	@Override
	public boolean isHostile() {
		return true;
	}
	
	@Override
	public float getCorpseChance() {
		return 0.5f;
	}
	
	@Override
	public int getBaseArmourClass() {
		return 7;
	}
	
	@EventHandler(selfOnly = true)
	public void onDamage(EntityDamagedEvent e) {
		getDungeon().logRandom("It whimpers.", "It whines.", "It cries.", "It yelps.");
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		EntityLiving kicker = e.getKicker();
		Noun name = getName(getDungeon().getPlayer());
		Noun kickerName = kicker.getName(getDungeon().getPlayer());
		
		if (e.isKickerPlayer()) {
			getDungeon().You(
				"kick %s!",
				Article.addTheIfPossible(name.clone(), false).build()
			);
		}
		
		int dodgeChance = 5;
		
		if (e.isKickerPlayer()) {
			Player player = (Player) kicker;
			int agility = player.getAttributes().getAttribute(Attribute.AGILITY);
			dodgeChance = (int) Math.ceil(agility / 3);
		}
		
		if (RandomUtils.roll(1, dodgeChance) == 1) {
			if (e.isKickerPlayer()) {
				getDungeon().orange(
					"%s dodges %s kick!",
					LanguageUtils.subject(this).build(Capitalise.first),
					LanguageUtils.victim(kicker)
				);
			}
			
			return;
		}
				
		int damageChance = 2;
		
		if (e.isKickerPlayer()) {
			Player player = (Player) kicker;
			int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
			damageChance = (int) Math.ceil(strength / 6) + 1;
		}
		
		if (RandomUtils.roll(1, damageChance) == 1) {
			damage(new DamageSource(kicker, null, DamageType.PLAYER_KICK), 1);
		}
		
		if (isAlive()) {
			if (RandomUtils.roll(1, 5) == 1) {
				getDungeon().orange(
					"%s bites %s foot!",
					LanguageUtils.subject(this).build(Capitalise.first),
					LanguageUtils.victim(kicker)
				);
				
				if (RandomUtils.roll(1, 4) == 1) {
					if (e.isKickerPlayer()) {
						getDungeon().redThe("bite was pretty deep!");
					}
					
					kicker.damage(new DamageSource(kicker, null, DamageType.KICK_REVENGE), 1);
					kicker.addStatusEffect(new InjuredFoot(getDungeon(), kicker, RandomUtils.roll(3, 6)));
				}
			} else if (RandomUtils.roll(1, 5) == 1) {
				getDungeon().orange(
					"%s yanks %s leg!",
					LanguageUtils.subject(this).build(Capitalise.first),
					LanguageUtils.victim(kicker)
				);
				
				if (RandomUtils.roll(1, 4) == 1) {
					if (e.isKickerPlayer()) {
						getDungeon().red("It strains your leg!");
					}
					
					kicker.damage(new DamageSource(kicker, null, DamageType.KICK_REVENGE), 1);
					kicker.addStatusEffect(new StrainedLeg(RandomUtils.roll(3, 6)));
				}
			}
		}
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
		return DamageType.CANINE_BITE;
	}
	
	@Override
	public Verb getMeleeAttackVerb(EntityLiving victim) {
		return Lexicon.bite.clone();
	}
}
