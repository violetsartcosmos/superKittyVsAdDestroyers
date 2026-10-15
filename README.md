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


Copy this summary into your next chat, then paste your complete current FirstScreen.java underneath it.
Project handoff: SuperKittyVsAdDestroyers
I’m a beginner building a Java game called SuperKittyVsAdDestroyers. Please teach me incrementally: explain what each change does, use my existing variable names, and give precise instructions about where to insert or replace code.
Environment
Windows.
IntelliJ IDEA.
Java/JDK 21.
libGDX project generated with gdx-liftoff-1.14.2.3.jar.
Game template.
Desktop platform: LWJGL3.
FreeType selected, though current text uses the default BitmapFont.
No third-party libraries.
Java package:
package com.superkittyvsaddestroyers.game;
Run the game through IntelliJ’s Gradle panel:
lwjgl3 → Tasks → application → run
Current structure
Gameplay is currently in:
public class FirstScreen implements Screen
It uses:
ShapeRenderer for placeholder characters, health bars, and an attack outline.
FitViewport with an 800 × 450 world.
SpriteBatch and BitmapFont for on-screen text.
Rectangle hitboxes.
Vector2 for movement.
No character artwork or animations yet. Keep the simple shapes while building gameplay.
Kitty movement
Important existing names:
SCREEN_SIZE_WIDTH
SCREEN_SIZE_HEIGHT
KITTY_SIZE
KITTY_SPEED

kitty_movement
kittyX
kittyY
kittyBounds
Kitty is a pink 40 × 40 square with white eyes.
Starts at (100, 100).
Moves with WASD or arrow keys.
Speed: 240 world units per second.
Movement uses normalization so diagonal movement isn’t faster:
kitty_movement.nor().scl(
KITTY_SPEED * Math.min(delta, 0.05f)
);
Kitty is clamped inside the world.
I’ve already learned the basics of normalization, scaling, and delta time.
Enemy movement and contact
Important names:
enemyAdBounds
touchingEnemyAd

ENEMY_AD_SPEED
enemyAdMovement

ENEMY_AD_MAX_HEALTH
enemyAdHealth
One Ad Destroyer, drawn as an orange 50 × 50 square.
Starts at (550, 220).
Maximum health: 3.
Speed: 100 world units per second.
Chases Kitty by calculating the vector from its center to Kitty’s center, normalizing it, and scaling by speed × elapsed time.
Movement step is limited to the remaining distance to avoid overshooting.
Enemy stays inside the world.
It turns red while touching a living Kitty.
Characters can overlap; there is no solid-body collision resolution.
A defeated enemy is not drawn, does not move, and cannot deal damage.
Kitty’s attack
Important names:
KITTY_ATTACK_REACH
KITTY_ATTACK_FLASH_DURATION
kittyAttackBounds
kittyAttackFlashTimer
Space attacks once per press using isKeyJustPressed().
Attack reaches 25 units beyond each side of Kitty.
With Kitty’s 40 × 40 body, attack bounds are 90 × 90.
Attack is omnidirectional for now.
Deals one damage immediately if the attack bounds overlap a living enemy.
Yellow attack outline lasts 0.12 seconds.
The outline is visual only; it does not cause damage throughout its duration.
No attack cooldown beyond requiring another key press.
Enemy has a green health bar with a dark background.
Three successful hits defeat the enemy.
Kitty health and defeat
Important names:
KITTY_MAX_HEALTH
KITTY_INVULNERABILITY_DURATION
kittyHealth
kittyInvulnerabilityTimer
Maximum health: 3.
Touching a living enemy causes one damage when protection has expired.
After damage, Kitty gets 1 second of invulnerability.
Kitty is white while protected, pink normally, and gray when defeated.
Kitty has a health bar at the upper-left.
At zero health, Kitty cannot move or attack, and the enemy stops chasing.
Contact damage is checked after attack processing, so an enemy defeated by Kitty’s attack that frame cannot hurt her.
Restart
R restarts the round:
Restores Kitty and enemy health.
Resets Kitty to (100, 100).
Resets enemy to (550, 220).
Clears movement vectors, attack flash, protection timer, and contact flag.
Resets score to zero.
There should be only one R-key handling block, at the beginning of render().
Most recently supplied changes: score and text
The previous response supplied these additions. Check my pasted code to confirm which are implemented and working.
Fields:
private SpriteBatch batch;
private BitmapFont font;

private int score = 0;

private static final int POINTS_PER_HIT = 10;
private static final int POINTS_PER_DEFEAT = 100;
Scoring:
10 points per successful hit.
Additional 100 points when enemy health reaches zero.
One three-hit enemy gives 130 total points.
Misses and attacks on a defeated enemy give no points.
R resets score.
Text drawn after all shape-rendering passes:
Kitty’s numeric health under her health bar.
Score near the upper-right.
Controls along the bottom.
"GAME OVER! Press R to restart." if Kitty is defeated.
Otherwise, "YOU WIN! Press R to play again." if the enemy is defeated.
Rendering details:
Batch uses viewport.getCamera().combined.
Shape rendering must finish before batch.begin().
Font and batch are initialized in show().
Shapes, batch, and font are disposed and set to null in dispose().
Next goal
I liked the suggestion to add multiple enemies or successive waves so the score can grow beyond one fight but first we need to plan out the placeholders and replace them with cute real sprites.
Please first inspect my current code, then suggest the smallest manageable next step toward that goal. Avoid jumping into a large rewrite or adding many features at once.
My pasted code is the source of truth if it differs from this summary.
Paste your current FirstScreen.java after that summary in the new chat.
