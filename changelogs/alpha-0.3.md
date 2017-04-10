# Gameplay
- Made all monsters use a stateful AI, and given them all traits
- Added ice rooms
- Added paralysis and fire status effects
- New HUD skin
- Character creation screen

# Minor changes
- Added a vsync setting
- Added a frame time counter
- Added an AI debugger
- Added a level debugger
- Changed the level extension to .save.gz
- Added an AO effect to the lighting
- Added reflections to water and ice
- Nerfed molds

# Bugfixes
- Fixed a bug where you couldn't diagonally attack monsters standing in a door
- Fixed the stupid water particles

# Code
- Moved everything out of Level into their own specialised Store classes
- Split GDXRenderer into Components
- Moved GDXRenderer into a GameScreen
- Added screens/states
- Made a global event system
- Moved all of Dungeon's Listener related stuff to Events
- Moved all of Entity's on* to Events
- Rewritten the Wish class
- Removed all getters and setters, and replaced them with Lombok's @Getter and @Setter annotations
- Replaced some constructors with Lombok's @AllArgsConstructor annotations
- Made a lot of coordinate things used Points instead
- Replaced java.awt.Color with our own Colour class
- Changed all the DamageSource stuff - DamageSource is now a box class, DamageType is the enum of damage types, 
  and DamageClass is the class of the damage (melee, ranged, magic)