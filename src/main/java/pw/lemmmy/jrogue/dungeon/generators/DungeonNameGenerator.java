package pw.lemmmy.jrogue.dungeon.generators;

import pw.lemmmy.jrogue.utils.StringReplacer;
import pw.lemmmy.jrogue.utils.Utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DungeonNameGenerator {
	private static final HashMap<String, String[]> NAME_OBJECTS = new HashMap<>();
	private static final String[] NAME_TEMPLATES = {
		"{roomType} of the {pronoun}",
		"{roomType} of the {pronounAdjective} {pronoun}",
		"The {roomAdjective} {roomType}",
		"The {roomType}",
		"The {pronoun}'s {roomType}",
		"The {pronoun}'s {roomAdjective} {roomType}",
		"The {pronounAdjective} {pronoun}'s {roomType}",
		"The {pronounAdjective} {pronoun}'s {roomAdjective} {roomType}"
	};

	static {
		NAME_OBJECTS.put("roomType", new String[]{
			"Burrows",
			"Caverns",
			"Cells",
			"Chambers",
			"Crypt",
			"Delves",
			"Dungeon",
			"Grotto",
			"Haunt",
			"Labyrinth",
			"Lair",
			"Maze",
			"Pits",
			"Point",
			"Quarters",
			"Tunnels",
			"Vaults"
		});

		NAME_OBJECTS.put("pronoun", new String[]{
			"Arachnid",
			"Army",
			"Basilisk",
			"Desert",
			"Dragon",
			"Eagle",
			"Horsemen",
			"Knight",
			"Legion",
			"Lion",
			"Ogre",
			"Paladin",
			"Phoenix",
			"Priest",
			"Queen",
			"Raven",
			"Scorpion",
			"Serpent",
			"Warlord",
			"Warrior",
			"Wizard",
			"Wolf"
		});

		NAME_OBJECTS.put("pronounAdjective", new String[]{
			"Barbaric",
			"Blooded",
			"Chaotic",
			"Crying",
			"Crystal",
			"Doomed",
			"Ebon",
			"Elemental",
			"Fallen",
			"Forgotten",
			"Frozen",
			"Furious",
			"Ghost",
			"Impostor",
			"Infernal",
			"Mystic",
			"Mythic",
			"Perished",
			"Phantom",
			"Shadow",
			"Shrieking",
			"Shrouded",
			"Shunned",
			"Storm",
			"Unspoken",
			"Vanishing"
		});

		NAME_OBJECTS.put("roomAdjective", new String[]{
			"Ancient",
			"Arid",
			"Bellowing",
			"Black",
			"Bleak",
			"Blue",
			"Brutal",
			"Burning Forest",
			"Dark",
			"Dead",
			"Dream",
			"Eastern",
			"False",
			"Forsaken",
			"Goblin",
			"Grey",
			"Hallucination",
			"Haunted",
			"Isolated",
			"Laughing Skulls",
			"Living Dead",
			"Lonely",
			"Mesmerising",
			"Mysterious",
			"Mystic",
			"Narrow",
			"Northern",
			"Orc",
			"Phantom",
			"Quiet",
			"Raging",
			"Red",
			"Rocking",
			"Sad",
			"Sanguine",
			"Scarlet",
			"Scheming",
			"Shrieking",
			"Sorrow",
			"Southern",
			"Turbulent",
			"Twilight",
			"Unknown",
			"Volcanic",
			"Western",
			"White Forest",
			"Wicked",
			"Windy",
			"Yawning"
		});
	}

	public static String generate() {
		return StringReplacer.replace(
			Utils.randomFrom(NAME_TEMPLATES),
			Pattern.compile("\\{(\\w+)}"),
			(Matcher m) -> Utils.randomFrom(NAME_OBJECTS.get(m.group(1)))
		);
	}
}
