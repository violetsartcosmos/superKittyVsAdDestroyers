# SuperKittyVsAdDestroyers

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and a main class extending `Game` that sets the first screen.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.



Java/JDK 21.

Run the game through IntelliJ’s Gradle panel:
lwjgl3 → Tasks → application → run

Kitty movement

Starts at (100, 100).
Moves with WASD or arrow keys.
Speed: 240 world units per second.
Movement uses normalization so diagonal movement isn’t faster:
kitty_movement.nor().scl(
KITTY_SPEED * Math.min(delta, 0.05f)
);
Enemy movement and contact


Starts at (550, 220).

Speed: 100 world units per second.
Chases Kitty by calculating the vector from its center to Kitty’s center, normalizing it, and scaling by speed × elapsed time.
Movement step is limited to the remaining distance to avoid overshooting.

It turns red while touching a living Kitty.
Characters can overlap; there is no solid-body collision resolution.
A defeated enemy is not drawn, does not move, and cannot deal damage.

Space attacks once per press using isKeyJustPressed().
Attack is omnidirectional.
Deals one damage immediately if the attack bounds overlap a living enemy.
Successful hits defeat the enemy.
Kitty health and defeat

Touching a living enemy causes one damage when protection has expired.
After damage, Kitty gets 1 second of invulnerability.
Contact damage is checked after attack processing, so an enemy defeated by Kitty’s attack that frame cannot hurt her.
Restart
R restarts the round:
Restores Kitty and enemy health.

Clears movement vectors, attack flash, protection timer, and contact flag.
Resets score to zero.
There should be only one R-key handling block, at the beginning of render().

Scoring:
10 points per successful hit.
Additional 100 points when enemy health reaches zero.
One three-hit enemy gives 130 total points.
Misses and attacks on a defeated enemy give no points.
Create the jar:
    ./gradlew lwjgl3:dist
Run the jar:
    java -jar "lwjgl3/build/libs/SuperKittyVsAdDestroyers-1.0.0.jar"
