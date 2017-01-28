# Gameplay

# Minor changes

# Bugfixes

# Code
- Moved everything out of Level into their own specialised Store classes
- Split GDXRenderer into Components
- Made a global event system
- Moved all of Dungeon's Listener related stuff to Events
- Moved all of Entity's on* to Events
- Rewritten the Wish class
- Removed all getters and setters, and replaced them with Lombok's @Getter and @Setter annotations
- Replaced some constructors with Lombok's @AllArgsConstructor annotations