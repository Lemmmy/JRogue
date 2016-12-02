package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;
import pw.lemmmy.jrogue.dungeon.entities.monsters.ai.GhoulAI;

import java.util.List;

public class MonsterHellhound extends MonsterHound {
	public MonsterHellhound(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		return null; // TODO: Fire
	}
	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Hellhound": "hellhound";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_HELLHOUND;
	}
}
