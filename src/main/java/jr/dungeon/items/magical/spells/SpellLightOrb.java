package jr.dungeon.items.magical.spells;

import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.QuickSpawn;
import jr.dungeon.entities.magic.EntityLightOrb;
import jr.dungeon.items.magical.DirectionType;
import jr.dungeon.items.magical.MagicalSchool;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.utils.VectorInt;

@Registered(id="spellLightOrb")
public class SpellLightOrb extends Spell {
    @Override
    public Noun getName() {
        return Lexicon.lightOrb.clone();
    }
    
    @Override
    public MagicalSchool getMagicalSchool() {
        return MagicalSchool.MATTER;
    }
    
    @Override
    public DirectionType getDirectionType() {
        return DirectionType.NON_DIRECTIONAL;
    }
    
    @Override
    public int getTurnsToRead() {
        return 2;
    }
    
    @Override
    public int getLevel() {
        return 1;
    }
    
    @Override
    public boolean canCastAtSelf() {
        return true;
    }
    
    public int getCastingCost() {
        return getLevel() * 2;
    }
    
    @Override
    public void castNonDirectional(EntityLiving caster) {
        QuickSpawn.spawnClass(EntityLightOrb.class, caster.getLevel(), caster.getPosition());
        caster.getDungeon().log("A great orb of light materialises and lights up the dungeon.");
    }
    
    @Override
    public void castDirectional(EntityLiving caster, VectorInt direction) {
        castNonDirectional(caster);
    }
}
