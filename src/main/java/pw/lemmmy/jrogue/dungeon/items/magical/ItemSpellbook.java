package pw.lemmmy.jrogue.dungeon.items.magical;

import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.Dungeon;
import pw.lemmmy.jrogue.dungeon.Prompt;
import pw.lemmmy.jrogue.dungeon.entities.EntityLiving;
import pw.lemmmy.jrogue.dungeon.entities.containers.Container;
import pw.lemmmy.jrogue.dungeon.entities.containers.EntityChest;
import pw.lemmmy.jrogue.dungeon.entities.player.Attribute;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.*;
import pw.lemmmy.jrogue.dungeon.items.Readable;
import pw.lemmmy.jrogue.dungeon.items.identity.AspectBookContents;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.Spell;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.SpellLightOrb;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.SpellStrike;
import pw.lemmmy.jrogue.utils.RandomUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemSpellbook extends Item implements Readable, SpecialChestSpawn {
	private static final Map<Integer, Class<? extends Spell>> spellLevelMap = new HashMap<>();
	
	static {
		spellLevelMap.put(0, SpellLightOrb.class);
		spellLevelMap.put(0, SpellStrike.class);
	}
	
	private Spell spell;
	
	private int timesRead = 0;
	private int readingProgress = 0;
	
	public ItemSpellbook() {
		super();
		
		addAspect(new AspectBookContents());
	}
	
	@Override
	public String getName(EntityLiving observer, boolean requiresCapitalisation, boolean plural) {
		String s = getBeatitudePrefix(observer, requiresCapitalisation);
		
		if (!s.isEmpty() && requiresCapitalisation) {
			requiresCapitalisation = false;
		}
			
		if (!isAspectKnown(observer, AspectBookContents.class)) {
			s += (requiresCapitalisation ? "Book" : "book") + (plural ? "s" : "");
			
			return s;
		}
		
		s += (requiresCapitalisation ? "S" : "s") + "pellbook of ";
		s += spell.getName(false);
		s += plural ? "s" : "";
		
		return s;
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
					reader.getDungeon().yellowYou("know [CYAN]%s[] well enough already.", spell.getName(false));
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
			
			reader.getDungeon().prompt(new Prompt(msg, new char[] {'y', 'n'}, true, new Prompt.SimplePromptCallback(reader.getDungeon()) {
				@Override
				public void onResponse(char response) {
					if (response != 'y') {
						return;
					}
					
					read(reader, chance, alreadyKnown.get(), (char) letter.get());
				}
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
					dungeon.turn();
					return;
				}
				
				lastHealth = reader.getHealth();
				
				dungeon.turn();
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
				
				dungeon.greenYou("refreshed your memory on [CYAN]%s[]!", spell.getName(false));
			} else {
				spell.setKnowledgeTimeout(20000);
				spell.setKnown(true);
				reader.getKnownSpells().put(letter, spell);
				
				dungeon.greenYou("learned [CYAN]%s[]!", spell.getName(false));
			}
			
		} else {
			dungeon.redYou("fail to read the book correctly!");
			
			// TODO: paralyse the player for turns - 2
		}
	}
	
	public Spell getSpell() {
		return spell;
	}
	
	public void setSpell(Spell spell) {
		this.spell = spell;
	}
	
	@Override
	public boolean shouldStack() {
		return false;
	}
	
	@Override
	public void serialise(JSONObject obj) {
		super.serialise(obj);
		
		JSONObject spellJSON = new JSONObject();
		spell.serialise(spellJSON);
		obj.put("spell", spellJSON);
		obj.put("spellClass", spell.getClass().getName());
		
		obj.put("timesRead", timesRead);
		obj.put("readingProgress", readingProgress);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void unserialise(JSONObject obj) {
		super.unserialise(obj);
		
		String spellClassName = obj.getString("spellClass");
		
		try {
			Class<? extends Spell> spellClass = (Class<? extends Spell>) Class.forName(spellClassName);
			Constructor<? extends Spell> spellConstructor = spellClass.getConstructor();
			spell = spellConstructor.newInstance();
			spell.unserialise(obj.getJSONObject("spell"));
		} catch (ClassNotFoundException e) {
			JRogue.getLogger().error("Unknown spell class {}", spellClassName);
		} catch (NoSuchMethodException e) {
			JRogue.getLogger().error("Spell class {} has no unserialisation constructor", spellClassName);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("Error loading spell class {}", spellClassName);
			JRogue.getLogger().error(e);
		}
		
		timesRead = obj.optInt("timesRead", 0);
		readingProgress = obj.optInt("readingProgress", 0);
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
		
		List<Class<? extends Spell>> spells = spellLevelMap.entrySet().stream()
			.filter(e -> e.getKey() <= player.getExperienceLevel())
			.filter(e -> player.getKnownSpells().values().stream()
							.noneMatch(s -> s.getClass().equals(e.getValue())))
			.map(Map.Entry::getValue)
			.collect(Collectors.toList());
		
		if (spells.size() == 0) {
			return;
		}
		
		try {
			Class<? extends Spell> spellClass = RandomUtils.randomFrom(spells);
			Constructor spellConstructor = spellClass.getConstructor();
			spell = (Spell) spellConstructor.newInstance();
			ItemStack stack = new ItemStack(this, 1);
			container.add(stack);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			JRogue.getLogger().error("iaugjhghaeruirgarefgyhwergb(4-	erhh");
			e.printStackTrace();
		}
	}
}
