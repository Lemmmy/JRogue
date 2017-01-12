package pw.lemmmy.jrogue.dungeon.items.magical;

import org.json.JSONObject;
import pw.lemmmy.jrogue.JRogue;
import pw.lemmmy.jrogue.dungeon.entities.player.Player;
import pw.lemmmy.jrogue.dungeon.items.Readable;

import pw.lemmmy.jrogue.dungeon.items.Item;
import pw.lemmmy.jrogue.dungeon.items.ItemAppearance;
import pw.lemmmy.jrogue.dungeon.items.ItemCategory;
import pw.lemmmy.jrogue.dungeon.items.magical.spells.Spell;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ItemSpellbook extends Item implements Readable {
	private Spell spell;
	
	@Override
	public String getName(boolean requiresCapitalisation, boolean plural) {
		if (!isIdentified()) {
			return (requiresCapitalisation ? "Book" : "book") + (plural ? "s" : "");
		}
		
		String name = (requiresCapitalisation ? "S" : "s") + "pellbook of ";
		name += spell.getName(false);
		name += plural ? "s" : "";
		
		return name;
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
	}
}
