# Gameplay
- HUD completely redesigned to show more information in a better way
- Added a map to the HUD
- Skills can now be levelled up
- New combat system: armour-class and to-hit rolls
 - Players and monsters with lower armour classes are harder to hit
 - You can now potentially miss monsters in combat
- New item identity system - you now know almost nothing about an item until you inspect those aspects.
- Item beatitude (blessed, uncursed and cursed) - BUC status is now an aspect
- Altars - dropping an item on them reveals item beatitude
- Magic - there are now spells and spellbooks
 - Strike spell, a big bolt of force that destroys entities and fragile objects
 - Light orb, a large ball of light to light up those dim corridors
- Ranged - there are now bows and arrows, and ranged combat
- Quaffing - you can now drink things including potions, and from fountains
- Reading - you can now read spellbooks
- Weapon racks - small containers that hold only weapons
- Candlesticks as small room decoration
- Rugs as larger room decoration
- Added ice levels below dlvl-10 with different monsters
- Added frozen fountains
- Added thermometers
- Added foxes
- Added lizards
- Added mold (red, yellow, green and blue, no effects yet)
- Items now age, and corpses will rot over time. Eating a rotten corpse will result in fatal food poisoning

# Minor changes
- Completely revamped the entity loop and action system to support multiple speeds better
- Changed monster spawning so that they now spawn in level ranges
- Some monsters (e.g. Jackals) now spawn in packs
- Effects of poison and mercury are different
- Corridors are now darker
- Kicking shatterable items causes them to be destroyed
- Better strength-based entity kicking chances

# Bugfixes
- **All** generated dungeons are now completable - a pathfinder makes its way through the level before presenting it to you
- Events that had nothing to do with the player would still log as message as if they did
- Lighting is now serialised as the player remembers it
- Items now properly stack, and can also be non-stackable
- You can no longer move diagonally through doors
- Fixed many container bugs related to items in the left and right hands
- Lighting is built at proper times now
- Renamed many items with strange names e.g. `corn on the cob` → `ear of corn`, `bread` → `loaf of bread`
- Renamed empty potions to glass bottles
- Entity depths are better now
- Bad food warning only appears on the first eat

# Code
- Moved everything into smaller categorised packages
- Greatly reduced the size of the Player class by moving all their methods into visitors (OOP visitor pattern)
- Pressure washed many classes including Prompt and Container
- Changed all the TileType stuff to have TileFlags, a bitfield of flags so that there are no crazy large if statements simply checking if the tile is a door of any state
- Completely refactored level generators so that many generators can share similar systems (e.g. rooms)
- Removed stupid Optional fields
- Levels have a climate option now
- Serialised objects now also have a dynamic layer of persistence, ready for extremely case-specific serialised fields, and also mods