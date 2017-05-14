package jr.language;

import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.player.Player;
import jr.dungeon.items.ItemStack;
import jr.language.transformations.Article;
import jr.language.transformations.Possessive;

public class LanguageUtils {
	public static Noun subject(Entity entity) {
		Player p = entity.getDungeon().getPlayer();
		
		return Article.addTheIfPossible(entity.getName(p).clone(), false);
	}
	
	public static Noun subjectPossessive(Entity entity) {
		Player p = entity.getDungeon().getPlayer();
		
		return entity.getName(p).clone()
			.addInstanceTransformer(Possessive.class, entity.getPossessiveTransformer(p));
	}
	
	public static Noun object(Entity entity) {
		Player p = entity.getDungeon().getPlayer();
		
		return Article.addTheIfPossible(entity.getName(p).clone(), false);
	}
	
	public static Noun anObject(Entity entity) {
		Player p = entity.getDungeon().getPlayer();
		
		return Article.addAIfPossible(entity.getName(p).clone());
	}
	
	public static Noun anObject(EntityLiving observer, ItemStack itemStack) {
		return Article.addAIfPossible(itemStack.getName(observer).clone());
	}
	
	public static Noun victim(Entity entity) {
		Player p = entity.getDungeon().getPlayer();
		
		return Article.addTheIfPossible(
			entity.getName(p).clone()
				.addInstanceTransformer(Possessive.class, entity.getPossessiveTransformer(p)),
			false
		);
	}
}
