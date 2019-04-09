package jr.dungeon.entities.containers;

import com.google.gson.annotations.Expose;
import jr.dungeon.Dungeon;
import jr.dungeon.Level;
import jr.dungeon.entities.Entity;
import jr.dungeon.entities.EntityAppearance;
import jr.dungeon.entities.EntityLiving;
import jr.dungeon.entities.events.EntityKickedEntityEvent;
import jr.dungeon.entities.events.EntityWalkedOnEvent;
import jr.dungeon.entities.interfaces.ContainerOwner;
import jr.dungeon.entities.interfaces.Lootable;
import jr.dungeon.events.EventHandler;
import jr.dungeon.items.ItemStack;
import jr.dungeon.items.Shatterable;
import jr.dungeon.serialisation.Registered;
import jr.dungeon.wishes.Wishable;
import jr.language.LanguageUtils;
import jr.language.Lexicon;
import jr.language.Noun;
import jr.language.transformers.Capitalise;
import jr.utils.Point;
import jr.utils.RandomUtils;
import lombok.Getter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Wishable(name="chest")
@Registered(id="entityChest")
public class EntityChest extends Entity implements Lootable, ContainerOwner {
	@Expose private Container container;
	@Expose	@Getter private boolean locked;
	
	public EntityChest(Dungeon dungeon, Level level, Point position) {
		super(dungeon, level, position);
		
		container = new Container(getName(null));
		locked = RandomUtils.rollD2();
	}
	
	protected EntityChest() { super(); }
	
	@Override
	public Noun getName(EntityLiving observer) {
		return Lexicon.chest.clone();
	}
	
	@Override
	public EntityAppearance getAppearance() {
		return EntityAppearance.APPEARANCE_CHEST;
	}
	
	@Override
	public int getDepth() {
		return 1;
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
	
	@Override
	public Optional<Container> getContainer() {
		return Optional.ofNullable(container);
	}

	@Override
	public boolean isLootable() {
		return !locked;
	}

	@Override
	public Optional<String> getLootSuccessString() {
		return Optional.of(String.format(
			"You open %s...",
			LanguageUtils.object(this)
		));
	}
	
	@Override
	public Optional<String> getLootFailedString() {
		return Optional.of(String.format(
			"%s is locked.",
			LanguageUtils.object(this).build(Capitalise.first)
		));
	}
	
	@EventHandler(selfOnly = true)
	public void onKick(EntityKickedEntityEvent e) {
		if (e.isKickerPlayer()) {
			boolean somethingShattered = false;
			
			List<Map.Entry<Character, ItemStack>> shatterableItems = container.getItems().entrySet().stream()
				.filter(i -> i.getValue()
					.getItem() instanceof Shatterable)
				.collect(Collectors.toList());
			
			for (Iterator<Map.Entry<Character, ItemStack>> iterator = shatterableItems.iterator(); iterator
				.hasNext(); ) {
				iterator.next();
				
				if (RandomUtils.roll(3) == 1) {
					// kicking chests has a high chance of damaging items regardless of skill.
					iterator.remove();
					somethingShattered = true;
				}
			}
			
			if (somethingShattered) {
				getDungeon().orangeYou("hear something shatter.");
			}
			
			if (locked && RandomUtils.roll(4) == 1) {
				getDungeon().green(
					"%s breaks open!",
					LanguageUtils.object(this).build(Capitalise.first)
				);
				locked = false;
			}
		}
	}
	
	@EventHandler(selfOnly = true)
	public void onWalk(EntityWalkedOnEvent e) {
		if (e.isWalkerPlayer()) {
			getDungeon().log(
				"There is %s here.",
				LanguageUtils.anObject(this)
			);
		}
	}
	
	@Override
	public boolean canBeWalkedOn() {
		return true;
	}
}
