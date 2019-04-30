package jr.dungeon.items.magical;

import com.google.gson.annotations.Expose;
import jr.ErrorHandler;
import jr.dungeon.Dungeon;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.containers.Container;
import jr.dungeon.entities.containers.EntityChest;
import jr.dungeon.entities.player.Attribute;
import jr.dungeon.entities.player.Player;
import jr.dungeon.io.YesNoPrompt;
import jr.dungeon.items.*;
import jr.dungeon.items.identity.AspectBookContents;
import jr.dungeon.items.magical.spells.Spell;
import jr.dungeon.items.magical.spells.SpellLightOrb;
import jr.dungeon.items.magical.spells.SpellStrike;
import jr.dungeon.serialisation.Registered;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.transformers.TransformerType;
import jr.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Registered(id="itemSpellbook")
public class ItemSpellbook extends Item implements ReadableItem, SpecialChestSpawn {
    private static final Map<Class<? extends Spell>, Integer> spellLevelMap = new HashMap<>();
    
    static {
        spellLevelMap.put(SpellLightOrb.class, 0);
        spellLevelMap.put(SpellStrike.class, 0);
    }
    
    @Expose @Setter private Spell spell;
    
    @Expose private int timesRead = 0;
    @Expose private int readingProgress = 0;
    
    public ItemSpellbook() {
        super();
        
        addAspect(new AspectBookContents());
    }
    
    @Override
    public Noun getBaseName(EntityLiving observer) {
        if (!isAspectKnown(observer, AspectBookContents.class)) {
            return Lexicon.book.clone();
        } else {
            return Lexicon.spellbook.clone()
                .addInstanceTransformer(SpellbookTransformer.class, (s, m) -> s + " of " + spell.getName());
        }
    }
    
    @Override
    public float getWeight() {
        return 50;
    }
    
    @Override
    public ItemAppearance getAppearance() {
        return ItemAppearance.APPEARANCE_SPELLBOOK;
    }
    
    @Override
    public ItemCategory getCategory() {
        return ItemCategory.SPELLBOOK;
    }
    
    @Override
    public void onRead(Player reader) {
        observeAspect(reader, AspectBookContents.class);
        
        AtomicBoolean cancelled = new AtomicBoolean(false);
        AtomicBoolean alreadyKnown = new AtomicBoolean(false);
        AtomicInteger letter = new AtomicInteger(reader.getAvailableSpellLetter());
        
        reader.getKnownSpells().entrySet().stream()
            .filter(e -> e.getValue().equals(spell))
            .findFirst()
            .ifPresent(e -> {
                alreadyKnown.set(true);
                letter.set(e.getKey());
                
                if (e.getValue().getKnowledgeTimeout() >= 1000) {
                    reader.getDungeon().yellowYou("know [CYAN]%s[] well enough already.", spell.getName());
                    cancelled.set(true);
                }
            });
        
        if (cancelled.get()) {
            return;
        }
        
        if (letter.get() == 0) {
            reader.getDungeon().yellowYou("can't learn any more spells.");
        }
        
        float chance = getReadingSuccessChance(reader);
        
        if (chance != 1f) {
            String msg = chance > 0.75f ?
                         "This spellbook is difficult to understand. Continue?" :
                         "This spellbook is very difficult to understand. Continue?";
            
            reader.getDungeon().prompt(new YesNoPrompt(msg, true, yes -> {
                if (yes) read(reader, chance, alreadyKnown.get(), (char) letter.get());
            }));
        } else {
            read(reader, chance, alreadyKnown.get(), (char) letter.get());
        }
    }
    
    private void read(Player reader, float chance, boolean alreadyKnown, char letter) {
        Dungeon dungeon = reader.getDungeon();
        
        float roll = RandomUtils.randomFloat();
        float turns = spell.getTurnsToRead();
        
        dungeon.You("start reading the book.");
        
        if (roll >= chance) {
            int lastHealth = reader.getHealth();
            
            for (int i = 0; i < turns; i++) {
                readingProgress++;
                
                if (reader.getHealth() < lastHealth) {
                    dungeon.You("stop reading the book.");
                    dungeon.turnSystem.turn();
                    return;
                }
                
                lastHealth = reader.getHealth();
                
                dungeon.turnSystem.turn();
            }
            
            dungeon.greenYou("finish reading the book.");
            
            if (++timesRead >= 4) {
                dungeon.orangeThe("book disappears into thin air!");
                
                reader.getContainer().ifPresent(c -> c.getItems().entrySet().stream()
                    .filter(e -> e.getValue().getItem().equals(ItemSpellbook.this))
                    .findFirst()
                    .ifPresent(e -> {
                        c.remove(e.getKey());
                    }));
            }
            
            if (alreadyKnown) {
                Spell playerSpell = reader.getKnownSpells().get(letter);
                playerSpell.setKnowledgeTimeout(20000);
                playerSpell.setKnown(true);
                
                dungeon.greenYou("refreshed your memory on [CYAN]%s[]!", spell.getName());
            } else {
                spell.setKnowledgeTimeout(20000);
                spell.setKnown(true);
                reader.getKnownSpells().put(letter, spell);
                
                dungeon.greenYou("learned [CYAN]%s[]!", spell.getName());
            }
            
        } else {
            dungeon.redYou("fail to read the book correctly!");
            
            // TODO: paralyse the player for turns - 2
        }
    }
    
    @Override
    public boolean shouldStack() {
        return false;
    }
    
    private float getReadingSuccessChance(Player player) {
        int intelligence = player.getAttributes().getAttribute(Attribute.INTELLIGENCE);
        int experience = player.getExperienceLevel();
        int level = spell.getLevel();
        
        return (intelligence + 4 + experience / 2 - 2 * level) / 30;
    }
    
    @Override
    public void onSpawnInChest(EntityChest chest, Container container) {
        // assign a random spell based on the player level when the level is generated
        // spells that the player already knows won't spawn
        
        Player player = chest.getDungeon().getPlayer();
        int playerLevel = player != null ? player.getExperienceLevel() : 1;
        
        List<Class<? extends Spell>> spells = spellLevelMap.entrySet().stream()
            .filter(e -> e.getValue() <= playerLevel)
            .filter(e -> player == null || player.getKnownSpells().values().stream()
                            .noneMatch(s -> s.getClass().equals(e.getKey())))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        if (spells.size() == 0) {
            return;
        }
        
        try {
            Class<? extends Spell> spellClass = RandomUtils.randomFrom(spells);
            assert spellClass != null;
            Constructor spellConstructor = spellClass.getConstructor();
            spell = (Spell) spellConstructor.newInstance();
            ItemStack stack = new ItemStack(this, 1);
            container.add(stack);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            ErrorHandler.error("Error spawning spellbook", e);
        }
    }
    
    public class SpellbookTransformer implements TransformerType {}
}
