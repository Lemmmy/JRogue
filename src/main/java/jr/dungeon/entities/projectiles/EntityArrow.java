package jr.dungeon.entities.projectiles;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.*;
import jr.dungeon.entities.player.Player;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.transformers.Capitalise;
import jr.utils.RandomUtils;

public class EntityArrow extends EntityProjectile {
	private boolean canPenetrate = false;
	
	public EntityArrow(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.arrow.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ARROW;
	}
	
	@Override
	public int getMovementSpeed() {
		return Dungeon.NORMAL_SPEED * 3;
	}
	
	public void setCanPenetrate(boolean penetrate) {
		canPenetrate = penetrate;
	}
	
	@Override
	public void onHitEntity(Entity victim) {
		if (victim instanceof EntityLiving) {
			Entity source = getSource();
			
			if (source != null && source instanceof EntityLiving) {
				EntityLiving living = (EntityLiving) victim;
				
				String colour = living instanceof Player ? "[ORANGE]" : "";
				
				getDungeon().log(
					"%s%s %s %s!",
					colour,
					LanguageUtils.subjectPossessive(this).build(Capitalise.first),
					LanguageUtils.autoTense(Lexicon.hit.clone(), this),
					LanguageUtils.object(living)
				);
				
				// TODO: pass bow item
				
				living.damage(new DamageSource(source, null, DamageType.ARROW), getArrowDamage());
				
				if (!canPenetrate) {
					killProjectile();
				}
			}
		}
	}
	
	private int getArrowDamage() {
		return RandomUtils.roll(6);
	}
	
	@Override
	public void killProjectile() {
		if (!isBeingRemoved() && RandomUtils.roll(3) != 3) {
			dropItems();
		}
		
		super.killProjectile();
	}
}
