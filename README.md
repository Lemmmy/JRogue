# JRogue
JRogue is a roguelike dungeon crawler written in Java using LibGDX.

## Building

### Prerequisites

* Git
* [Git LFS](https://git-lfs.github.com/)
* JDK 8
* Gradle (optional, wrapper is included in repo)

### Build steps

1. Make sure that [Git LFS](https://git-lfs.github.com/) is installed ***before*** cloning JRogue.
2. Clone the repository: `git clone https://github.com/Lemmmy/JRogue`
3. Navigate to the game directory: `cd JRogue`
4. Run `./gradlew run` to run the game.

## Contributing

**Note:** Currently, only IntelliJ is supported.

1. Make sure that [Git LFS](https://git-lfs.github.com/) is installed ***before*** cloning JRogue.
2. Download the [Lombok plugin](https://projectlombok.org/setup/intellij) for IntelliJ.
3. Clone the repository: `git clone https://github.com/Lemmmy/JRogue`
4. Open the project's `build.gradle` file in IntelliJ, enable auto-import in the Gradle dialog, and hit OK.
5. Enable annotation processors (required to get the project to compile):
   File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors. 
   Check "Enable annotation processing".
6. Run the "Run JRogue" configuration to run the game.

## Credits

* [Lemmmy](https://github.com/Lemmmy) &ndash; Programming, art
* [Lignum](https://github.com/Lignum) &ndash; Programming
* [3d6](https://github.com/BTCTaras) &ndash; Programming, art

All contributors listed at [here](https://github.com/Lemmmy/JRogue/graphs/contributors).

## Licence

This project is licenced under the [MIT License](LICENCE).