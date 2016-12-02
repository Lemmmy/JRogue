package pw.lemmmy.jrogue.dungeon.entities.monsters;

import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Level;
import pw.lemmmy.jrogue.dungeon.entities.EntityAppearance;
import pw.lemmmy.jrogue.dungeon.entities.LivingEntity;
import pw.lemmmy.jrogue.dungeon.entities.effects.StatusEffect;

import java.util.List;

public class MonsterIcehound extends MonsterHound {
	public MonsterIcehound(Dungeon dungeon, Level level, int x, int y) {
		super(dungeon, level, x, y);
	}

	@Override
	public List<StatusEffect> getCorpseEffects(LivingEntity victim) {
		return null; // TODO: Ice
	}
	@Override
	public String getName(boolean requiresCapitalisation) {
		return requiresCapitalisation ? "Icehound": "icehound";
	}

	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_ICEHOUND;
	}
}
