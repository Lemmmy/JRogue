package jr.dungeon.entities.monsters.ai.stateful.familiar;

import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.containers.EntityItem;
import jr.dungeon.entities.events.EntityMovedEvent;
import jr.dungeon.entities.monsters.Monster;
import jr.dungeon.entities.monsters.ai.stateful.AITrait;
import jr.dungeon.entities.player.Player;
import jr.dungeon.events.EventHandler;
import jr.dungeon.items.identity.AspectBeatitude;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.transformers.Capitalise;

public class TraitAvoidCursed extends AITrait<FamiliarAI> {
	/**
	 * Intrinsic or extrinsic pieces of information that can affect the way a {@link FamiliarAI} behaves.
	 *
	 * @param ai The {@link FamiliarAI} that hosts this trait.
	 */
	public TraitAvoidCursed(FamiliarAI ai) {
		super(ai);
	}
	
	@EventHandler(selfOnly = true)
	private void onStepOnCursed(EntityMovedEvent event) {
		Monster m = getMonster();
		Level l = m.getLevel();
		Dungeon d = m.getDungeon();
		Player p = d.getPlayer();
		
		if (l != p.getLevel()) return;
		if (event.getLastPosition() == event.getNewPosition()) return;
		
		l.entityStore.getEntitiesAt(event.getNewPosition()).stream()
			.filter(EntityItem.class::isInstance)
			.map(EntityItem.class::cast)
			.filter(e -> e.getItem().getAspect(AspectBeatitude.class).isPresent())
			.filter(e -> !e.getItem().isAspectKnown(p, AspectBeatitude.class))
			.filter(e -> ((AspectBeatitude) e.getItem().getAspect(AspectBeatitude.class).get()).getBeatitude()
				== AspectBeatitude.Beatitude.CURSED)
			.findFirst()
			.ifPresent(e -> {
				d.log(
					"%s %s only reluctantly.",
					LanguageUtils.subject(m).build(Capitalise.first),
					LanguageUtils.autoTense(Lexicon.move.clone(), getMonster())
				);
				
				e.getItem().observeAspect(p, AspectBeatitude.class);
			});
	}
	
	@Override
	public void update() {
		
	}
}
