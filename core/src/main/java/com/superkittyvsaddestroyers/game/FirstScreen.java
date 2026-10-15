package com.superkittyvsaddestroyers.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

/** Home Screen */
public class FirstScreen implements Screen {
    private static final float SCREEN_SIZE_WIDTH = 800f;
    private static final float SCREEN_SIZE_HEIGHT = 450f;
    private static final float KITTY_SIZE = 120f;
    private static final float KITTY_SPEED = 240f;
    private static final int ENEMY_AD_MAX_HEALTH = 3;
    private static final int KITTY_MAX_HEALTH = 20;
    private static final float KITTY_INVULNERABILITY_DURATION = 5f;

    private int kittyHealth = KITTY_MAX_HEALTH;
    private float kittyInvulnerabilityTimer = 0f;

    private ShapeRenderer shapes;
    private FitViewport viewport;

    //    Super Kitty
    private final Vector2 kitty_movement = new Vector2();

    private float kittyX = 100f;
    private float kittyY = 100f;
    private final Rectangle kittyBounds = new Rectangle();

    // EnemyAd

    private final Rectangle enemyAdBounds =
        new Rectangle(550f, 220f, 50f, 50f);

    private boolean touchingEnemyAd;

    // Kitty can reach 25 units beyond each side of her body because she's super!
    private static final float KITTY_ATTACK_REACH = 25f;
    private static final float KITTY_ATTACK_FLASH_DURATION = 0.12f;

    private final Rectangle kittyAttackBounds = new Rectangle();
    private float kittyAttackFlashTimer = 0f;

    // Enemy health.
    private int enemyAdHealth = ENEMY_AD_MAX_HEALTH;
    private static final float ENEMY_AD_SPEED = 100f;

    private final Vector2 enemyAdMovement = new Vector2();

    private SpriteBatch batch;
    private BitmapFont font;
    private Texture kittyTexture;

    private int score = 0;

    private static final int POINTS_PER_HIT = 10;
    private static final int POINTS_PER_DEFEAT = 100;

    @Override
    public void show() {
        // To start with we create shapes and viewport
        if (shapes == null) {
            shapes = new ShapeRenderer();
            viewport = new FitViewport(SCREEN_SIZE_WIDTH, SCREEN_SIZE_HEIGHT);
        }

        if (batch == null) {
            batch = new SpriteBatch();
            font = new BitmapFont();
            font.setColor(Color.WHITE);

            kittyTexture = new Texture(Gdx.files.internal("kittyTiny.png"));
        }

        viewport.update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            true
        );
    }

    @Override
    public void render(float delta) {
        // Restart the whole round.
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            kittyHealth = KITTY_MAX_HEALTH;
            kittyInvulnerabilityTimer = 0f;
            kittyAttackFlashTimer = 0f;

            kittyX = 100f;
            kittyY = 100f;
            kitty_movement.setZero();

            enemyAdHealth = ENEMY_AD_MAX_HEALTH;
            enemyAdBounds.setPosition(550f, 220f);
            enemyAdMovement.setZero();
            score = 0;

            touchingEnemyAd = false;
        }

        // Count down Kitty's protection after taking damage.
        kittyInvulnerabilityTimer = Math.max(
            0f,
            kittyInvulnerabilityTimer - delta
        );
        // Read keyboard input.
        kitty_movement.set(0f, 0f);

        if (Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            kitty_movement.x -= 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)
            || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            kitty_movement.x += 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)
            || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            kitty_movement.y += 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)
            || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            kitty_movement.y -= 1f;
        }

        /* Normalize so diagonal kitty_movement isn't faster.
            By pressing both directions it would technically go about 1.5x times faster using the
            Pythagorean theorem length = sqrt(x^2 + y^2) to calculate that

            nor() makes the direction vector length 1.

            PLAYER_SPEED * Math.min(delta, 0.05f)
            calculates the distance to move this frame.

            scl(...) multiplies both vector components by that distance.
        */

        if (kittyHealth > 0) {
            kitty_movement.nor().scl(
                KITTY_SPEED * Math.min(delta, 0.05f)
            );
        } else {
            kitty_movement.setZero();
        }

        kittyX += kitty_movement.x;
        kittyY += kitty_movement.y;

        // Restrict Super Kitty inside the game area.
        kittyX = MathUtils.clamp(
            kittyX, 0f, SCREEN_SIZE_WIDTH - KITTY_SIZE
        );
        kittyY = MathUtils.clamp(
            kittyY, 0f, SCREEN_SIZE_HEIGHT - KITTY_SIZE
        );

        // Match the player's hitbox to its current position.
        kittyBounds.set(kittyX, kittyY, KITTY_SIZE, KITTY_SIZE);

        // Check whether the two hitboxes overlap.
        touchingEnemyAd = kittyBounds.overlaps(enemyAdBounds);

        // Living enemies move toward Kitty.
        if (enemyAdHealth > 0 && kittyHealth > 0) {
            float kittyCenterX = kittyX + KITTY_SIZE / 2f;
            float kittyCenterY = kittyY + KITTY_SIZE / 2f;

            float enemyCenterX =
                enemyAdBounds.x + enemyAdBounds.width / 2f;
            float enemyCenterY =
                enemyAdBounds.y + enemyAdBounds.height / 2f;

            // Direction from the enemy's center to Kitty's center.
            enemyAdMovement.set(
                kittyCenterX - enemyCenterX,
                kittyCenterY - enemyCenterY
            );

            float distanceToKitty = enemyAdMovement.len();

            // Don't move farther than the remaining distance.
            float moveDistance = Math.min(
                ENEMY_AD_SPEED * Math.min(delta, 0.05f),
                distanceToKitty
            );

            enemyAdMovement.nor().scl(moveDistance);

            enemyAdBounds.x += enemyAdMovement.x;
            enemyAdBounds.y += enemyAdMovement.y;

            // Keep the enemy inside the game area.
            enemyAdBounds.x = MathUtils.clamp(
                enemyAdBounds.x,
                0f,
                SCREEN_SIZE_WIDTH - enemyAdBounds.width
            );

            enemyAdBounds.y = MathUtils.clamp(
                enemyAdBounds.y,
                0f,
                SCREEN_SIZE_HEIGHT - enemyAdBounds.height
            );
        }
        // Only living enemies count as touching Kitty.
        touchingEnemyAd =
            enemyAdHealth > 0 && kittyBounds.overlaps(enemyAdBounds);

        // Keep the attack area centered around Kitty.
        kittyAttackBounds.set(
            kittyX - KITTY_ATTACK_REACH,
            kittyY - KITTY_ATTACK_REACH,
            KITTY_SIZE + KITTY_ATTACK_REACH * 2f,
            KITTY_SIZE + KITTY_ATTACK_REACH * 2f
        );

        // Count down the visual attack flash.
        kittyAttackFlashTimer = Math.max(
            0f, kittyAttackFlashTimer - delta
        );

        // Attack once each time Space is pressed.
        if (kittyHealth > 0
            && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            kittyAttackFlashTimer = KITTY_ATTACK_FLASH_DURATION;

            if (enemyAdHealth > 0
                && kittyAttackBounds.overlaps(enemyAdBounds)) {

                enemyAdHealth--;
                score += POINTS_PER_HIT;

                if (enemyAdHealth == 0) {
                    score += POINTS_PER_DEFEAT;
                }

                Gdx.app.log(
                    "Combat",
                    "Ad Destroyer health: " + enemyAdHealth
                        + " | Score: " + score
                );
            }
        }

        // Refresh contact after attacks, in case the enemy was defeated.
        touchingEnemyAd =
            kittyHealth > 0
                && enemyAdHealth > 0
                && kittyBounds.overlaps(enemyAdBounds);

        // Kitty can take damage only when her protection has expired.
        if (touchingEnemyAd && kittyInvulnerabilityTimer <= 0f) {
            kittyHealth--;

            kittyInvulnerabilityTimer =
                KITTY_INVULNERABILITY_DURATION;

            Gdx.app.log("Combat", "Kitty health: " + kittyHealth);

            if (kittyHealth == 0) {
                kittyAttackFlashTimer = 0f;
                Gdx.app.log("Combat", "Kitty defeated! Press R to restart.");
            }
        }

        // Clear the previous frame and prepare the camera.
        ScreenUtils.clear(0.08f, 0.09f, 0.14f, 1f);
        viewport.apply();
        shapes.setProjectionMatrix(viewport.getCamera().combined);

        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // Only draw the enemy while it is alive.
        if (enemyAdHealth > 0) {
            if (touchingEnemyAd) {
                shapes.setColor(Color.RED);
            } else {
                shapes.setColor(Color.ORANGE);
            }

            shapes.rect(
                enemyAdBounds.x,
                enemyAdBounds.y,
                enemyAdBounds.width,
                enemyAdBounds.height
            );

            // Health bar background.
            float healthBarY = enemyAdBounds.y + enemyAdBounds.height + 8f;

            shapes.setColor(Color.DARK_GRAY);
            shapes.rect(
                enemyAdBounds.x,
                healthBarY,
                enemyAdBounds.width,
                6f
            );

            // Remaining health.
            float healthFraction =
                (float) enemyAdHealth / ENEMY_AD_MAX_HEALTH;

            shapes.setColor(Color.GREEN);
            shapes.rect(
                enemyAdBounds.x,
                healthBarY,
                enemyAdBounds.width * healthFraction,
                6f
            );
        }

        // Kitty's health bar in the top-left of the game area.
        shapes.setColor(Color.DARK_GRAY);
        shapes.rect(20f, SCREEN_SIZE_HEIGHT - 30f, 120f, 12f);

        float kittyHealthFraction =
            (float) kittyHealth / KITTY_MAX_HEALTH;

        shapes.setColor(Color.RED);
        shapes.rect(
            20f,
            SCREEN_SIZE_HEIGHT - 30f,
            120f * kittyHealthFraction,
            12f
        );

        shapes.end();

        // Draw the attack outline in a separate line-rendering pass.
        if (kittyAttackFlashTimer > 0f) {
            shapes.begin(ShapeRenderer.ShapeType.Line);

            shapes.setColor(Color.YELLOW);
            shapes.rect(
                kittyAttackBounds.x,
                kittyAttackBounds.y,
                kittyAttackBounds.width,
                kittyAttackBounds.height
            );

            shapes.end();
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        if (kittyHealth <= 0) {
            batch.setColor(Color.GRAY);
        } else if (kittyInvulnerabilityTimer > 0f) {
            batch.setColor(1f, 1f, 1f, 0.6f);
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(
            kittyTexture,
            kittyX,
            kittyY,
            KITTY_SIZE,
            KITTY_SIZE
        );

        batch.setColor(Color.WHITE);
        font.setColor(Color.WHITE);

        // Health
        font.draw(
            batch,
            "Kitty health: " + kittyHealth + " / " + KITTY_MAX_HEALTH,
            20f,
            SCREEN_SIZE_HEIGHT - 45f
        );

        // Score in the upper-right
        font.draw(
            batch,
            "Score: " + score,
            SCREEN_SIZE_WIDTH - 180f,
            SCREEN_SIZE_HEIGHT - 20f
        );

        // Controls near the bottom
        font.draw(
            batch,
            "WASD / Arrows: move    Space: attack    R: restart",
            20f,
            25f
        );

        // Display outcome.
        if (kittyHealth <= 0) {
            font.setColor(Color.RED);
            font.draw(
                batch,
                "GAME OVER! Press R to restart.",
                260f,
                SCREEN_SIZE_HEIGHT / 2f
            );
        } else if (enemyAdHealth <= 0) {
            font.setColor(Color.GREEN);
            font.draw(
                batch,
                "YOU WIN! Press R to play again.",
                260f,
                SCREEN_SIZE_HEIGHT / 2f
            );
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if (width <= 0 || height <= 0) {
            return;
        }

        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        if (shapes != null) {
            shapes.dispose();
            shapes = null;
        }

        if (kittyTexture != null) {
            kittyTexture.dispose();
            kittyTexture = null;
        }

        if (batch != null) {
            batch.dispose();
            batch = null;
        }

        if (font != null) {
            font.dispose();
            font = null;
        }
    }
}
