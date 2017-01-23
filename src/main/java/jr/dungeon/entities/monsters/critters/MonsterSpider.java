package jr.dungeon.entities.monsters.critters;

import jr.dungeon.entities.DamageSource;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.actions.ActionMelee;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.utils.RandomUtils;
import org.json.JSONObject;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.actions.EntityAction;
import jr.dungeon.entities.effects.StatusEffect;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.GhoulAI;
import jr.dungeon.tiles.TileType;

import java.util.List;

public class MonsterSpider extends Monster {
	private int speed;
	
	public MonsterSpider(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y, 1);
		
		speed = Dungeon.NORMAL_SPEED - RandomUtils.random(6);
		
		setAI(new GhoulAI(this));
		getAI().addAvoidTile(TileType.TILE_GROUND_WATER);
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Spider" : "spider";
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
	public int getNutrition() {
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
	
	@Override
	protected void onDamage(DamageSource damageSource, int damage, EntityLiving attacker, boolean isPlayer) {}
	
	@Override
	protected void onKick(EntityLiving kicker, boolean isPlayer, int dx, int dy) {
		if (isPlayer) {
			getDungeon().You("step on the %s!", getName(kicker, false));
		}
		
		int damageChance = 2;
		
		if (isPlayer) {
			Player player = (Player) kicker;
			int strength = player.getAttributes().getAttribute(Attribute.STRENGTH);
			damageChance = (int) Math.ceil(strength / 6) + 1;
		}
		
		if (RandomUtils.roll(1, damageChance) == 1) {
			damage(DamageSource.PLAYER_KICK, 1, kicker, isPlayer);
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
	public void meleeAttack(EntityLiving victim) {
		setAction(new ActionMelee(
			getDungeon().getPlayer(),
			DamageSource.SPIDER_BITE,
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